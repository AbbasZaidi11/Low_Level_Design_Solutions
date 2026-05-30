package org.example.vendingmachinestates.impl;

import org.example.context.Item;
import org.example.context.VendingMachine;
import org.example.vendingmachinestates.Coin;
import org.example.vendingmachinestates.State;

import java.util.*;

public class SelectionState extends State {

    public SelectionState(){
        System.out.println("Currently Vending Machine is in SelectionState");
    }

    @Override
    public void chooseProduct(VendingMachine machine, int codeNumber) throws Exception{
        //1. get item of this codeNumber
        Item item = machine.getInventory().getItem(codeNumber);

        //2. total amount paid by user
        int paidByUser = 0;
        for(Coin coin:machine.getCoinList()){
            paidByUser = paidByUser + coin.value;
        }

        //3. compare product price and amount paid by user
        if(paidByUser < item.getPrice()){
            System.out.println("Insufficient Amount, Product you select is for price: " + item.getPrice()+" and you paid: "+paidByUser);
            refundFullMoney(machine);
            throw new Exception("Insufficient amount");
        }else if(paidByUser >= item.getPrice()){
            if(paidByUser > item.getPrice()){
                getChange(paidByUser-item.getPrice());
            }
            machine.setVendingMachineState(new DispenseState(machine,codeNumber));
        }
    }

    @Override
    public int getChange(int returnExtraMoney) throws Exception{
        //actual logic should be to return coins in the dispense tray, but for simplicity i am just returning the amount to be refunded
        System.out.println("Returned the change in the Coin Dispense Tray: " + returnExtraMoney);
        return returnExtraMoney;
    }

    @Override
    public List<Coin> refundFullMoney(VendingMachine machine) throws Exception{
        System.out.println("Returned the full amount back in the Coin Dispense Tray");
        machine.setVendingMachineState(new IdleState(machine));
        return machine.getCoinList();
    }

}
