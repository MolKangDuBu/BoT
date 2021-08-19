'use strict';

const express = require('express');
const router = express.Router();

const { FileSystemWallet, Gateway } = require('fabric-network');
const fs = require('fs');
const path = require('path');
var net = require('net');

const ccpPath = path.resolve(__dirname, '..', '..', 'basic-network', 'connection.json');
const ccpJSON = fs.readFileSync(ccpPath, 'utf8');
const ccp = JSON.parse(ccpJSON);

const port = 22485;
const host = "192.168.0.11";


/* GET users listing. */
router.get('/', async (req, res) => {
  res.send('respond with a resource');
});


router.post('/addIot', async (req, res) => {
  const args = [req.body.device, req.body.id, req.body.area, req.body.lamp, req.body.gas, req.body.tmp, req.body.hum, req.body.feedback];
  try {
    const result = await callChainCode('addIot', true, ...args);
    if (result == 'error occurred!!!') {
      res.status(200).send('addIot_Failed');
    } else {
      res.status(200).send('addIot_Success');
    }
  } catch(err) {
    console.log(err);
    res.status(200).send('addIot_Failed');
  }
});

router.post('/lampStateUpdate', async (req, res) => {
  const args = [req.body.devicekey, req.body.lamp];

  try {
    var socket = net.connect({
      port: port,
      host: host
    });
    
    socket.setEncoding('utf8');

    // Arduino connection
    socket.on('connect', () => {
      console.log("connected to server");
  
      if(req.body.lamp == 'true') {
        setTimeout( () => {
            socket.write('ON');
        }, 1000);
      } else if(req.body.lamp == 'false') {
        setTimeout( () => {
          socket.write('OFF');
        }, 1000);
      }      
      setTimeout( () => {
        socket.destroy()
      }, 2000);      
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
    
    const result = await callChainCode('lampStateUpdate', true, ...args);
    if (result == 'error occurred!!!') {
      res.status(200).send('lampStateUpdate_Failed');
    } else {
      res.status(200).send('lampStateUpdate_Success');
    }
  } catch(err) {
    console.log(err);
    res.status(200).send('lampStateUpdate_Failed');
  }
});

/*
router.post('/gasStateUpdate', async (req, res) => {
  const args = [req.body.devicekey, req.body.gas];
  try {
    const result = await callChainCode('gasStateUpdate', true, ...args);
    if (result == 'error occurred!!!') {
      res.status(200).send('gasStateUpdate_Success');
    } else {
      res.status(200).send('gasStateUpdate_Failed');
    }
  } catch(err) {
    console.log(err);
    res.status(200).send('gasStateUpdate_Failed');
  }
});

router.post('/tmpHumStateUpdate', async (req, res) => {
  const args = [req.body.devicekey, req.body.tmp, req.body.hum];
  try {
    const result = await callChainCode('tmpHumStateUpdate', true, ...args);
    if (result == 'error occurred!!!') {
      res.status(200).send('tmpHumStateUpdate_Failed');
    } else {
      res.status(200).send('tmpHumStateUpdate_Success');
    }
  } catch(err) {
    console.log(err);
    res.status(200).send('tmpHumStateUpdate_Failed');
  }
});
*/

router.post('/iotFind', async (req, res) => {
  const args = [req.body.userId];
  try {
    const result = await callChainCode('iotFind', false, ...args);
    if (result == 'error occurred!!!') {
      res.status(200).send('iotFind_Failed');
    } else {
      res.status(200).json(JSON.parse(result));
    }
  } catch(err) {
    console.log(err);
    res.status(200).send('iotFind_Failed');
  }
});

router.post('/queryAllIoTs', async (req, res) => {
  try {
    const result = await callChainCode('queryAllIoTs', false);
    if (result == 'error occurred!!!') {
      res.status(200).send('queryAllIoTs_Failed');
    } else {
      res.json(JSON.parse(result));
    }
  } catch(err) {
    console.log(err);
    res.status(200).send('queryAllIoTs_Failed');
  }
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