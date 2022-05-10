import * as https from 'https';
import { readFile , readFileSync} from 'fs';
import * as WebSocket from 'ws';
import { v4 as uuid } from 'uuid';
import { Server } from './server';

const server = https.createServer({
    key: readFileSync('./privkey.pem', 'utf8'),
    cert: readFileSync('./fullchain.pem', 'utf8')
}, (req, res) => {
    if (req.url?.endsWith('/')) req.url += 'index.html';
    readFile(__dirname.replace('/out', '/public') + req.url, (err, data) => {
        if (err) {
            res.writeHead(404);
            res.end(JSON.stringify(err));
            return;
        }
        res.writeHead(200);
        res.end(data);
    });
});

const wss = new WebSocket.Server({ server });
const PORT = process.env.PORT || 3000;

let ids: { [key: string]: Server } = {};

wss.on('connection', (ws: WebSocket) => {

    ws.on('message', (msg: string) => {
        msg = msg.toString();
        console.log(`Received: ${msg}`);
        if (msg.startsWith('ESTABLISH SERVER')) {
            let id = uuid().split('-')[0]; // Create new id
            if (msg.startsWith('ESTABLISH SERVER:')) { // If server has already established an id
                id = msg.replace('ESTABLISH SERVER:', ''); // Use that ID
            }
            ids[id] = new Server(ws, id); // Register the server to our network
        } else if (msg.startsWith('CONNECT:')) {
            let id = msg.replace('CONNECT:', '');
            if (ids[id]) {
                ids[id].clients.push(ws);
            } else {
                ws.send(JSON.stringify({ type: 'error', error: 'Id does not exist' }));
            }
        }
    });

    ws.send('OK');
    console.log(`New connection established`);
});

server.listen(PORT, () => {
    console.log(`Server started on port ${PORT} :)`);
});