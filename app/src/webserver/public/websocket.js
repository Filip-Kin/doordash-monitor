let ws;

function startConnection() {
    document.documentElement.webkitRequestFullScreen();
    id = document.getElementById('device-id').value;
    localStorage.setItem('id', id);

    createWebSocket(id);

    connectButton.parentElement.parentElement.style.display = 'none';
    document.getElementById('offer-ui').style.display = 'block';
}

function createWebSocket(id) {
    ws = new WebSocket(`wss://${new URL(window.location).hostname}:9008`);
    ws.addEventListener('message', (evt) => {
        if (evt.data === 'OK') {
            M.toast({html: 'Connected to server'});
            return ws.send(`CONNECT:${id}`);
        }
        handleMessage(evt);
    });

    ws.addEventListener('close', () => {
        if (navigator.onLine) {
            ws = null;
            setTimeout(() => createWebSocket(id), 500);
        }
    });
}

window.addEventListener('online', startConnection);

function handleMessage(evt) {
    let msg = JSON.parse(evt.data);
    if (msg.type === 'paraoffer') {
        updateOfferUI(msg);
    } else if (msg.type === 'address') {
        document.getElementById('delivery-housenumber').innerHTML = msg.address;
    } else if (msg.type === 'looking') {
        document.getElementById('delivery-housenumber').innerHTML = '';
        //updateOfferUI({reset: true});
    } else if (msg.type === 'offer') {

    }
}