const { test } = require("node:test");
const assert = require("node:assert/strict");
const { readFileSync } = require("node:fs");
const { randomUUID } = require("node:crypto");
const { join } = require("node:path");
const { createMockUserToken } = require("@firebase/util");

const project = "demo-quartz-messaging";
test("sender and receiver can query their notifications; an unrelated reader cannot read one", {
  skip: !process.env.FIRESTORE_EMULATOR_HOST || process.env.GCLOUD_PROJECT !== project
}, async () => {
  const base = "http://" + process.env.FIRESTORE_EMULATOR_HOST;
  const rules = readFileSync(join(__dirname, "firestore-merged.rules"), "utf8");
  const installed = await fetch(base + "/emulator/v1/projects/" + project + ":securityRules", {
    method: "PUT", headers: {"Content-Type": "application/json"},
    body: JSON.stringify({rules: {files: [{name: "firestore.rules", content: rules}]}})
  });
  assert.equal(installed.status, 200, await installed.text());
  const {initializeApp, deleteApp} = require("firebase-admin/app");
  const app = initializeApp({projectId: project}, "waitlist-rules-test");
  const db = require("firebase-admin/firestore").getFirestore(app);
  const sender = randomUUID(), receiver = randomUUID(), outsider = randomUUID();
  const doc = db.collection("messages").doc();
  await doc.create({type: "notification", senderId: sender, receiverId: receiver, message: "Gate opens at noon."});
  const api = base + "/v1/projects/" + project + "/databases/(default)/documents";
  const headers = uid => ({Authorization: "Bearer " + createMockUserToken({sub: uid}, project),
    "Content-Type": "application/json"});
  for (const [field, uid] of [["senderId", sender], ["receiverId", receiver], ["receiverId", outsider]]) {
    const where = {fieldFilter: {field: {fieldPath: field}, op: "EQUAL", value: {stringValue: uid}}};
    const res = await fetch(api + ":runQuery", {method: "POST", headers: headers(uid),
      body: JSON.stringify({structuredQuery: {from: [{collectionId: "messages"}],
        where}})});
    const rows = await res.json();
    assert.equal(res.status, 200, JSON.stringify(rows));
    assert.equal(rows.some(row => row.document?.name.endsWith("/" + doc.path)), uid !== outsider);
  }
  assert.equal((await fetch(api + "/" + doc.path, {headers: headers(outsider)})).status, 403);
  // Exercise the unprivileged client write path with the same fields and timestamp transform as Android.
  const site = db.collection("campsites").doc(), draw = db.collection("Lotteries").doc();
  const booking = db.collection("Reservations").doc();
  await Promise.all([site.set({id: site.id, ownerId: sender, name: "Pine"}),
    draw.set({campsites: [site], entrants: [db.doc("users/" + receiver)]}),
    booking.set({campsiteId: site.id, entrantId: receiver}),
    ...[sender, receiver, outsider].map(uid => db.doc("users/" + uid).set({name: uid}))]);
  async function checkQuery(uid, collectionId, fieldPath, op, value, expectedStatus) {
    const res = await fetch(api + ":runQuery", {method: "POST", headers: headers(uid),
      body: JSON.stringify({structuredQuery: {from: [{collectionId}], where: {fieldFilter:
        {field: {fieldPath}, op, value: typeof value === "string" ? {stringValue: value} : value}}}})});
    const result = await res.json();
    assert.equal(res.status, expectedStatus, JSON.stringify(result));
    if (expectedStatus === 200) assert.ok(result.some(row => row.document));
  }
  await checkQuery(sender, "Reservations", "campsiteId", "EQUAL", site.id, 200);
  await checkQuery(receiver, "Reservations", "entrantId", "EQUAL", receiver, 200);
  await checkQuery(outsider, "Reservations", "campsiteId", "EQUAL", site.id, 403);
  const notice = {senderId: sender, senderName: sender, receiverId: receiver, message: "Meet at noon.",
    title: "Pine", type: "notification", kind: "waitlist_notice", campsiteId: site.id, lotteryId: draw.id,
    broadcastId: randomUUID(), entrantIndex: 0, siteIndex: 0};
  const bookMail = {senderId: receiver, senderName: receiver, receiverId: sender, message: "Check-in?",
    title: "Pine", type: "direct", reservationId: booking.id};
  const prefix = "projects/" + project + "/databases/(default)/documents/";
  function write(path, data, timestamp) {
    const fields = Object.fromEntries(Object.entries(data).map(([k,v]) => [k,
      typeof v === "number" ? {integerValue: String(v)} : {stringValue: v}]));
    const op = {update: {name: prefix + path, fields}};
    if (timestamp) op.updateTransforms = [{fieldPath: "sentAt", setToServerValue: "REQUEST_TIME"}];
    return op;
  }
  async function commit(uid, writes, status) {
    const res = await fetch(api + ":commit", {method: "POST", headers: headers(uid), body: JSON.stringify({writes})});
    assert.equal(res.status, status, await res.text());
  }
  const context = uid => "users/" + uid + "/messagingContext/campsite";
  await commit(sender, [write(context(sender), {siteId: site.id})], 200);
  await commit(outsider, [write(context(outsider), {siteId: site.id})], 403);
  const siteQuery = {arrayValue: {values: [{referenceValue: prefix + site.path}, {stringValue: site.id},
    {mapValue: {fields: {id: {stringValue: site.id}}}},
    {mapValue: {fields: {id: {stringValue: site.id}, ownerId: {stringValue: sender}, name: {stringValue: "Pine"}}}}]}};
  await checkQuery(sender, "Lotteries", "campsites", "ARRAY_CONTAINS_ANY", siteQuery, 200);
  await checkQuery(outsider, "Lotteries", "campsites", "ARRAY_CONTAINS_ANY", siteQuery, 403);
  const historic = await db.collection("Lotteries").add({campsites: [{id: site.id, name: "Old name"}], entrants: []});
  const {indexEmbeddedCampsites} = require("./indexEmbeddedCampsites");
  assert.equal(await indexEmbeddedCampsites(db, false), 1);
  assert.equal(await indexEmbeddedCampsites(db, true), 1);
  assert.equal(await indexEmbeddedCampsites(db, true), 0);
  await checkQuery(sender, "Lotteries", "messagingCampsiteIds", "ARRAY_CONTAINS", site.id, 200);
  await checkQuery(outsider, "Lotteries", "messagingCampsiteIds", "ARRAY_CONTAINS", site.id, 403);
  assert.equal((await fetch(api + "/" + historic.path, {headers: headers(sender)})).status, 200);
  const receipt = "users/" + sender + "/messageRequests/" + randomUUID();
  const batch = [write(receipt, {body: notice.message, target: site.id, count: 1}),
    write("messages/" + randomUUID(), notice, true)];
  await commit(sender, batch, 200);
  // Only the sender can recover the committed receipt on retry; no overwrite is allowed.
  assert.equal((await fetch(api + "/" + receipt, {headers: headers(sender)})).status, 200);
  assert.equal((await fetch(api + "/" + receipt, {headers: headers(outsider)})).status, 403);
  await commit(sender, batch, 403);
  for (const change of [{receiverId: outsider}, {kind: "lottery_won"}, {senderId: ""}, {entrantIndex: 5}]) {
    await commit(sender, [write("messages/" + randomUUID(), {...notice, ...change}, true)], 403);
  }
  await commit(outsider, [write("messages/" + randomUUID(), {...notice, senderId: outsider, senderName: outsider}, true)], 403);
  await commit(receiver, [write("messages/" + randomUUID(), bookMail, true)], 200);
  await commit(receiver, [write("messages/" + randomUUID(), {...bookMail, receiverId: outsider}, true)], 403);
  await draw.update({entrants: []});
  await commit(sender, [write("messages/" + randomUUID(), notice, true)], 403);
  await site.update({ownerId: outsider});
  assert.equal((await fetch(api + "/" + draw.path, {headers: headers(sender)})).status, 403);
  await commit(outsider, [write(context(outsider), {siteId: site.id})], 200);
  assert.equal((await fetch(api + "/" + draw.path, {headers: headers(outsider)})).status, 200);
  await db.terminate();
  await deleteApp(app);
});
