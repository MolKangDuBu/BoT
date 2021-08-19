const net = require('net');

const port = 22485;
const host = "192.168.0.11";


var socket = net.connect({
    port: port,
    host: host
});

socket.setEncoding('utf8');

// Arduino connection
socket.on('connect', () => {
    console.log("connected to server");

    
    setTimeout( () => {
        socket.write('ON');
    }, 1000);

    if(type == 'true') {
        setTimeout( () => {
            socket.write('ON');
        }, 1000);
    } else if(type == 'false') {
        setTimeout( () => {
            socket.write('OFF');
        }, 1000);
    }      
});

socket.on('data', (data) => {
    console.log(data);
});

socket.on('close', () => {
    console.log('close');
});


socket.on('error', (err) => {
    console.log('on error: ', err.code);
});