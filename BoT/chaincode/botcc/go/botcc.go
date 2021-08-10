// package name
package main

 // module import
import (
	"bytes"
	"fmt"
	"strconv"
	"time"
	"encoding/json"
 	
	"github.com/hyperledger/fabric/core/chaincode/shim"
	"github.com/hyperledger/fabric/protos/peer"
 )
 
 // class, struct define
type SmartContract struct {
 }

type User struct {
	Pw		string `json:"pw"`
	Name	string `json:"name"`
	Mail	string `json:"mail"`
	Phone	string `json:"phone"`
	Team	string `json:"team"`
} 

type IoT struct {
	Area	string `json:"area"`
	Id		string `json:"id"`
	Lamp	string `json:"lamp"`
	Gas		string `json:"gas"`
	Tmp		string `json:"colour"`
	Hum		string `json:"hum"`
 } 

 // Init function
func (t *SmartContract) Init(stub shim.ChaincodeStubInterface) peer.Response {
	return shim.Success(nil)
}

 // Invoke function
func (t *SmartContract) Invoke(stub shim.ChaincodeStubInterface) peer.Response {

	fn, args := stub.GetFunctionAndParameters()

	if fn == "addUser" {
		return t.addUser(stub, args)
	} else if fn == "login" {
		return t.login(stub, args)
	} else if fn == "queryAllUsers" {
		return t.queryAllUsers(stub)
	} else if fn == "queryUser" {
		return t.queryUser(stub, args)
	} 

	return shim.Error("Invalid Smart Contract function name.")
}


 // addUser function
 func (t *SmartContract) addUser(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 6 {
		return shim.Error("Incorrect number of arguments. Expecting 6")
	}

	userAsBytes, err := stub.GetState(args[0])
	if(err != nil) {
		return shim.Error("Already Existing ID.")
	}

	var user = User{Pw: args[1], Name: args[2], Mail: args[3], Phone: args[4], Team: args[5]}

	userAsBytes, _ = json.Marshal(user)
	stub.PutState(args[0], userAsBytes)

	return shim.Success(nil)
}


//login function
func (t *SmartContract) login(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if(len(args) != 2) {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	userAsBytes, _ := stub.GetState(args[0])
	return shim.Success(userAsBytes)
}


func (t *SmartContract) queryAllUsers(APIstub shim.ChaincodeStubInterface) peer.Response {

	startKey := "USER0"
	endKey := "USER999"

	resultsIterator, err := APIstub.GetStateByRange(startKey, endKey)
	if err != nil {
		return shim.Error(err.Error())
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing QueryResults
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		queryResponse, err := resultsIterator.Next()
		if err != nil {
			return shim.Error(err.Error())
		}
		// Add a comma before array members, suppress it for the first array member
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"Id\":")
		buffer.WriteString("\"")
		buffer.WriteString(queryResponse.Key)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Record\":")
		// Record is a JSON object, so we write as-is
		buffer.WriteString(string(queryResponse.Value))
		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")
	
	fmt.Printf("- queryAllUsers:\n%s\n", buffer.String())

	return shim.Success(buffer.Bytes())
}

func (s *SmartContract) queryUser(APIstub shim.ChaincodeStubInterface, args []string) peer.Response {

	if len(args) != 1 {
		return shim.Error("Incorrect number of arguments. Expecting 1")
	}

	userAsBytes, _ := APIstub.GetState(args[0])
	return shim.Success(userAsBytes)
}



 // setAsset function
func setAsset(stub shim.ChaincodeStubInterface, args []string) peer.Response {
	if len(args) != 2 {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}

	err2 := stub.PutState(args[0], []byte(args[1]))
	if err2 != nil {
		return shim.Error("Incorrect number of arguments. Expecting 2")
	}
	return shim.Error("Incorrect number of arguments. Expecting 2")
}

 // getAsset function
func getAsset(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	if len(args) != 1 {
		return "", fmt.Errorf("Incorrect args. Expecting a key")
	}
	value, err := stub.GetState(args[0])
	if err != nil {
		return "", fmt.Errorf("Failed to get asset: %s with error: %s", args[0], err)
	}
	if value == nil {
		return "", fmt.Errorf("Asset not found: %s", args[0])
	}
	return string(value), nil
}

 // getHistory function
func getHistory(stub shim.ChaincodeStubInterface, args []string) (string, error) {
	if len(args) < 1 {
		return "", fmt.Errorf("Incorrect number of arguments. Expecting 1")
	}
	keyName := args[0]
	// 로그 남기기
	fmt.Println("getHistoryForKey:" + keyName)

	resultsIterator, err := stub.GetHistoryForKey(keyName)
	if err != nil {
		return "", err
	}
	defer resultsIterator.Close()

	// buffer is a JSON array containing historic values for the marble
	var buffer bytes.Buffer
	buffer.WriteString("[")

	bArrayMemberAlreadyWritten := false
	for resultsIterator.HasNext() {
		response, err := resultsIterator.Next()
		if err != nil {
			return "", err
		}
		if bArrayMemberAlreadyWritten == true {
			buffer.WriteString(",")
		}
		buffer.WriteString("{\"TxId\":")
		buffer.WriteString("\"")
		buffer.WriteString(response.TxId)
		buffer.WriteString("\"")

		buffer.WriteString(", \"Value\":")
		if response.IsDelete {
			buffer.WriteString("null")
		} else {
			buffer.WriteString(string(response.Value))
		}

		buffer.WriteString(", \"Timestamp\":")
		buffer.WriteString("\"")
		buffer.WriteString(time.Unix(response.Timestamp.Seconds, int64(response.Timestamp.Nanos)).String())
		buffer.WriteString("\"")

		buffer.WriteString(", \"IsDelete\":")
		buffer.WriteString("\"")
		buffer.WriteString(strconv.FormatBool(response.IsDelete))
		buffer.WriteString("\"")

		buffer.WriteString("}")
		bArrayMemberAlreadyWritten = true
	}
	buffer.WriteString("]")

	// logging
	fmt.Println("getHistoryForKey returning:\n" + buffer.String() + "\n")

	return (string)(buffer.Bytes()), nil
}


 // main function
func main() {	
	err := shim.Start(new(SmartContract))
	if err != nil {
		fmt.Println("Error creating new Smart Contract : %s", err)
	}
}

