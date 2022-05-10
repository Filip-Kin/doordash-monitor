function padDecimal(str) {
    let parts = str.toString().split('.');
    if (parts[1] == undefined) parts[1] = '00';
    return `${parts[0]}.${parts[1].padEnd(2, '0')}`;
}

let id = localStorage.getItem('id');
if (id) {
    document.getElementById('device-id').value = id;
    M.updateTextFields();
}

let connectButton = document.getElementById('connect-btn')
connectButton.addEventListener('click', startConnection);


function updateOfferUI(offer) {
    if (offer.reset) {
        document.getElementById('offer-amount').innerHTML = "Looking for order";
        document.getElementById('offer-tip').innerHTML = "";
        document.getElementById('offer-store').innerHTML = "";
        document.getElementById('offer-drivetime').innerHTML = "";
        document.getElementById('offer-hourly').innerHTML = "";
        document.getElementById('offer-subtotal').innerHTML = "";
    } else if (offer.type === 'paraoffer') {
        document.getElementById('offer-amount').innerHTML = `$${padDecimal(offer.amount)}`;
        document.getElementById('offer-tip').innerHTML = `$${padDecimal(offer.tip)} ${offer.confident ? '&#10024;' : '&#10067;'}`;
        document.getElementById('offer-store').innerHTML = offer.store;
        document.getElementById('offer-drivetime').innerHTML = `${offer.driveTime} Mins`;
        document.getElementById('offer-hourly').innerHTML = `$${padDecimal(offer.hourly)}/hr`;
        document.getElementById('offer-subtotal').innerHTML = `$${padDecimal(offer.subtotal)}`;
    } else if (offer.type === 'offer') {
        document.getElementById('offer-distance').innerHTML = offer.distance;
        document.getElementById('offer-permile').innerHTML = offer.perMile;
        document.getElementById('offer-items').innerHTML = offer.items;
        document.getElementById('offer-store').innerHTML = offer.store;
    }
}
