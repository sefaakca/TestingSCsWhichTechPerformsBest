/**
 *Submitted for verification at Etherscan.io on 2020-01-03
*/

pragma solidity ^0.5.0;

contract simplecontract {
    uint256 public a;
    
    function update(uint256 _a) public {
        a = _a;
    }
}