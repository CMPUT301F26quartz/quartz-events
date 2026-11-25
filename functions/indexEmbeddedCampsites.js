// One-time compatibility for lotteries containing old copies of campsite profiles.
// Running without --apply reports how many documents would change.
async function indexEmbeddedCampsites(db, apply) {
  let changed = 0;
  const draws = db.collection("Lotteries").select("campsites", "messagingCampsiteIds").stream();
  for await (const draw of draws) {
    function embeddedIds(snapshot) {
      const links = snapshot.get("campsites");
      if (!Array.isArray(links)) return [];
      return [...new Set(links.filter(link => link && typeof link === "object" && !link.path)
        .map(link => link.id).filter(id => typeof id === "string" && id && !id.includes("/")))];
    }
    function missing(snapshot) {
      const indexed = snapshot.get("messagingCampsiteIds") || [];
      return embeddedIds(snapshot).filter(id => !indexed.includes(id));
    }
    if (!missing(draw).length) continue;
    if (!apply) { changed++; continue; }
    const updated = await db.runTransaction(async tx => {
      const latest = await tx.get(draw.ref);
      if (!latest.exists || !missing(latest).length) return false;
      tx.update(draw.ref, {messagingCampsiteIds: embeddedIds(latest)});
      return true;
    });
    if (updated) changed++;
  }
  return changed;
}

module.exports = {indexEmbeddedCampsites};
if (require.main === module) {
  const [projectId, mode] = process.argv.slice(2);
  if (!projectId || (mode && mode !== "--apply")) throw Error("Usage: node indexEmbeddedCampsites.js PROJECT_ID [--apply]");
  const {initializeApp} = require("firebase-admin/app");
  const {getFirestore} = require("firebase-admin/firestore");
  const db = getFirestore(initializeApp({projectId}));
  indexEmbeddedCampsites(db, mode === "--apply")
    .then(count => console.log(count + (mode ? " lotteries updated." : " lotteries would be updated.")))
    .catch(error => { console.error(error.message); process.exitCode = 1; })
    .finally(() => db.terminate());
}
