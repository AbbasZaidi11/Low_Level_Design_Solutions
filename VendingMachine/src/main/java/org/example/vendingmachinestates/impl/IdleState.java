package org.example.vendingmachinestates.impl;

import org.example.context.Item;
import org.example.context.VendingMachine;
import org.example.vendingmachinestates.State;

import java.util.ArrayList;

public class IdleState extends State {

    public IdleState(){
        System.out.println("Currently vending machine is in IdleState");
    }

    public IdleState(VendingMachine machine){
        System.out.println("Currently vending machine is in IdleState");
        machine.setCoinList(new ArrayList<>());
    }

    @Override
    public void clickOnInsertCoinButton(VendingMachine machine) throws Exception{
        machine.setVendingMachineState(new HasMoneyState());
    }

    @Override
    public void updateInventory(VendingMachine machine, Item item, int codeNumber) throws Exception {
        machine.getInventory().addItem(item, codeNumber);
    }
}
