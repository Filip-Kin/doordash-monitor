const express = require('express');
const app = express();
const port = process.env.PORT || 3000;

let ids = {};

app.post('/app/:id/:ip', (req, res) => {
    console.log(`New id ${req.params.id} -> ${req.params.ip}`);
    if (ids[req.params.id] != undefined && ids[req.params.id] != req.params.ip) {
        res.status(400);
        res.send('ID in use');
        return;
    }
    ids[req.params.id] = req.params.ip;
    res.send(req.params.id)
});

app.get('/app/:id', (req, res) => {
    console.log(`Looking for id ${req.params.id}`);
    if (ids[req.params.id] == undefined) {
        res.status(404);
        res.send('ID not found');
        return;
    }
    res.send(ids[req.params.id]);
});

app.use(express.static('./public'));

app.listen(port, () => {
    console.log(`App listening on port ${port}`)
});
