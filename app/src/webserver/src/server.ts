import * as WebSocket from 'ws';

export class Server {
    public clients: Array<WebSocket> = [];
    public server: WebSocket;
    public id: string;

    constructor (server: WebSocket, id: string) {
        this.server = server;
        this.id = id;
        
        server.on('close', () => {
            this.broadcast({type: 'event', event: 'Server Closed'});
            for (let c of this.clients) {
                c.close();
            }
        });

        server.on('message', (msg: string) => {
            let json = JSON.parse(msg);
            if (['paraoffer', 'address', 'offer', 'looking'].includes(json.type)) return this.broadcast(json);
        });

        server.send(`OK:${id}`);
    }

    broadcast(msg: Object):void {
        for (let c of this.clients) {
            c.send(JSON.stringify(msg));
        }
    }
}