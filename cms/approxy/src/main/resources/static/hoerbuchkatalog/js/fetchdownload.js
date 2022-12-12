// Step 1: start the fetch and obtain a reader
let response = await fetch(url.toString(), {
    'method': 'GET',
    'mode': 'cors',
    'headers': {
        'Accept': 'application/json',
        'X-Bookworm-Mandant': this.mandant,
        'X-Bookworm-Hoerernummer': this.hoerernummer
    },
    'redirect': 'follow'
});
const reader = response.body.getReader();
// Step 2: get total length
const contentLength = +response.headers.get('Content-Length');
// Step 3: read the data
let receivedLength = 0; // received that many bytes at the moment
let chunks = []; // array of received binary chunks (comprises the body)
while(true) {
    const {done, value} = await reader.read();
    if (done) {
        break;
    }
    chunks.push(value);
    receivedLength += value.length;
    console.log(`Received ${receivedLength} of ${contentLength}`)
}
// Step 4: concatenate chunks into single Uint8Array
let chunksAll = new Uint8Array(receivedLength); // (4.1)
let position = 0;
for(let chunk of chunks) {
    chunksAll.set(chunk, position); // (4.2)
    position += chunk.length;
}
// Step 5: decode into a string
let result = new TextDecoder("utf-8").decode(chunksAll);
// We're done!
let commits = JSON.parse(result);
alert(commits[0].author.login);
