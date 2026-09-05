package org.example;

import org.example.context.*;
import org.example.vendingmachinestates.Coin;

public class VendingMachineAppDemo {

    public static void main(String[] args) {

        try {
            VendingMachine machine = new VendingMachine();

            // ---------------- Fill Inventory ----------------
            Item coke = new Item();
            coke.setType(ItemType.COKE);
            coke.setPrice(30);

            Item pepsi = new Item();
            pepsi.setType(ItemType.PEPSI);
            pepsi.setPrice(20);

            // Machine starts in IdleState
            machine.getVendingMachineState().updateInventory(machine, coke, 101);
            machine.getVendingMachineState().updateInventory(machine, pepsi, 102);

            // ---------------- Customer starts transaction ----------------

            // Press Insert Coin button
            machine.getVendingMachineState().clickOnInsertCoinButton(machine);

            // Insert coins
            machine.getVendingMachineState().insertCoin(machine, Coin.QUARTER); // 25
            machine.getVendingMachineState().insertCoin(machine, Coin.NICKEL);  // 5

            // Total = 30

            // Press Select Product button
            machine.getVendingMachineState().clickOnStartProductSelectionButton(machine);

            // Select Coke
            machine.getVendingMachineState().chooseProduct(machine, 101);

        } catch (Exception e) {
            System.out.println(e.getMessage());
        }
    }
}