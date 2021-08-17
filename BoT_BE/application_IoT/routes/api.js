'use strict';

const express = require('express');
const router = express.Router();

const { FileSystemWallet, Gateway } = require('fabric-network');
const fs = require('fs');
const path = require('path');
var net = require('net');

const ccpPath = path.resolve(__dirname, '..', '..', 'basic-network', 'connection_org2.json');
const ccpJSON = fs.readFileSync(ccpPath, 'utf8');
const ccp = JSON.parse(ccpJSON);

var socket = net.connect({
  port: 22485,
  host: "192.168.35.18"
});

socket.setEncoding('utf8');


/* GET users listing. */
router.get('/', async (req, res) => {
  res.send('respond with a resource');
});


router.post('/addIot', async (req, res) => {
  const args = [req.body.device, req.body.id, req.body.area, req.body.lamp, req.body.gas, req.body.tmp, req.body.hum, req.body.date, req.body.feedback];
  try {
    await callChainCode('addIot', true, ...args);
    res.status(200).send({ msg: 'addIot_Success' });
  } catch(err) {
    console.log(err);
    res.status(500).send({ msg: 'addIot_Failed'});
  }
});

router.post('/lampStateUpdate', async (req, res) => {
  const args = [req.body.devicekey, req.body.lamp];
  try {
    await callChainCode('lampStateUpdate', true, ...args);

    // Arduino lamp connect
    socket.on('connect', function() {
      console.log("on connect");
  
      if(req.body.lamp == 'ON') {
        setTimeout(() => {
            socket.write('ON');
        }, 1000);
      } else if(req.body.lamp == 'OFF') {
        setTimeout(() => {
          socket.write('OFF');
        }, 5000);
      }      
    });
    socket.on('data', function(data) {
      console.log(data);
    });

    socket.on('close', function() {
      console.log('close');
    });


    socket.on('error', function(err) {
      console.log('on error: ', err.code);
    });
  
    res.status(200).send({ msg: 'Transaction has been submitted.' });

  } catch(err) {
    console.log(err);
    res.status(500).send({ msg: 'Failed to submit transaction'});
  }
});

router.post('/gasStateUpdate', async (req, res) => {
  const args = [req.body.devicekey, req.body.gas];
  try {
    await callChainCode('gasStateUpdate', true, ...args);
    res.status(200).send({ msg: 'Transaction has been submitted.' });
  } catch(err) {
    console.log(err);
    res.status(500).send({ msg: 'Failed to submit transaction'});
  }
});

router.post('/tmpHumStateUpdate', async (req, res) => {
  const args = [req.body.devicekey, req.body.tmp, req.body.hum];
  try {
    await callChainCode('tmpHumStateUpdate', true, ...args);
    res.status(200).send({ msg: 'Transaction has been submitted.' });
  } catch(err) {
    console.log(err);
    res.status(500).send({ msg: 'Failed to submit transaction'});
  }
});

router.get('/queryAllIoTs', async (req, res) => {
  const result = await callChainCode('queryAllIoTs', false);
  res.json(JSON.parse(result));
});

// Call Chaincode
async function callChainCode(fnName, isSubmit, ...args) {
  try {
    // Create a new file system based wallet for managing identities.
    const walletPath = path.join(process.cwd(), 'wallet');
    const wallet = new FileSystemWallet(walletPath);
    console.log(`Wallet path: ${walletPath}`);

    // Check to see if we've already enrolled the user.
    const userExists = await wallet.exists('user2');
    if (!userExists) {
        console.log('An identity for the user "user2" does not exist in the wallet');
        console.log('Run the registerUser.js application before retrying');
        return;
    }

    // Create a new gateway for connecting to our peer node.
    const gateway = new Gateway();
    await gateway.connect(ccp, { wallet, identity: 'user2', discovery: { enabled: false } });

    // Get the network (channel) our contract is deployed to.
    const network = await gateway.getNetwork('mychannel');

    // Get the contract from the network.
    const contract = network.getContract('botcc');

    let result;
    if(isSubmit) {
      result = await contract.submitTransaction(fnName, ...args);
      console.log('Transaction has been submitted.');
    } else {
      result = await contract.evaluateTransaction(fnName, ...args);
      console.log(`Transaction has been evaluated. result: ${result.toString()}`);
    }
    return result;

  } catch(err) {
    console.error(`Failed to create transaction: ${err}`);
    return 'error occurred!!!';
  }
}

module.exports = router;