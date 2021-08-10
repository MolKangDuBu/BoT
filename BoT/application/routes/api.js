'use strict';

const express = require('express');
const router = express.Router();

const { FileSystemWallet, Gateway } = require('fabric-network');
const fs = require('fs');
const path = require('path');

const ccpPath = path.resolve(__dirname, '..', '..', 'basic-network', 'connection.json');
const ccpJSON = fs.readFileSync(ccpPath, 'utf8');
const ccp = JSON.parse(ccpJSON);

/* GET users listing. */
router.get('/', function(req, res, next) {
  res.send('respond with a resource');
});

router.post('/addUser', async (req, res) => {
    const args = [req.body.id, req.body.pw, req.body.name, req.body.mail, req.body.phone, req.body.team];
    try {
      await callChainCode('addUser', true, ...args);
      res.status(200).send({ msg: 'addUser_Success' });
    } catch(err) {
      console.log(err);
      res.status(500).send({ msg: 'addUser_Failed'});
    }
});

router.post('/login', async (req, res) => {
    const args = [req.body.id, req.body.pw];

    try {
        const result = await callChainCode('login', false, ...args);
        const obj = JSON.parse(result)

        if(req.body.pw == obj.pw) {
            res.status(200).send({ msg: 'login_Success' });
        } else {
            res.status(200).send({ msg: 'login_Failed' });
        }

    } catch(err) {
            res.status(400).send(null);
    }
});

router.get('/queryAllUsers', async (req, res) => {
    const result = await callChainCode('queryAllUsers', false);
    res.json(JSON.parse(result));
});

router.get('/queryUser', async (req, res) => {
    const userId = req.query.userId;
    try {
      const result = await callChainCode('queryUser', false, userId);
      res.json(JSON.parse(result));
    } catch(err) {
      res.status(400).send(null);
    }
});

/*  
router.get('/cars/:carNo', async (req, res) => {
  const carNo = req.params.carNo;
  try {
    const result = await callChainCode('queryCar', false, carNo);
    res.json(JSON.parse(result));
  } catch(err) {
    res.status(400).send(null);
  }
});

router.post('/cars', async (req, res) => {
  const args = [req.body.carNo, req.body.make, req.body.model, req.body.colour, req.body.owner];
  try {
    await callChainCode('createCar', true, ...args);
    res.status(200).send({ msg: 'Transaction has been submitted.' });
  } catch(err) {
    console.log(err);
    res.status(500).send({ msg: 'Failed to submit transaction'});
  }
});

router.put('/cars/:carNo', async (req, res) => {
  const args = [req.params.carNo, req.body.owner];
  try {
    await callChainCode('changeCarOwner', true, ...args);
    res.status(200).send({ msg: 'Transaction has been submitted.' });
  } catch(err) {
    console.log(err);
    res.status(500).send({ msg: 'Failed to submit transaction'});
  }
});
*/


async function callChainCode(fnName, isSubmit, ...args) {
  try {
    // Create a new file system based wallet for managing identities.
    const walletPath = path.join(process.cwd(), 'wallet');
    const wallet = new FileSystemWallet(walletPath);
    console.log(`Wallet path: ${walletPath}`);

    // Check to see if we've already enrolled the user.
    const userExists = await wallet.exists('user1');
    if (!userExists) {
        console.log('An identity for the user "user1" does not exist in the wallet');
        console.log('Run the registerUser.js application before retrying');
        return;
    }

    // Create a new gateway for connecting to our peer node.
    const gateway = new Gateway();
    await gateway.connect(ccp, { wallet, identity: 'user1', discovery: { enabled: false } });

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
    console.error(`Failed to create transaction: ${error}`);
    return 'error occurred!!!';
  }
}

module.exports = router;