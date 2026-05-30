package org.example.ATMStates;

import org.example.ATMRoomComponents.ATM;
import org.example.ATMRoomComponents.Card;

public class IdleState extends ATMState {

    @Override
    public void insertCard(ATM atm, Card card) {
        System.out.println("Card is inserted");
        atm.setCurrentATMState(new HasCardState());
    }
}