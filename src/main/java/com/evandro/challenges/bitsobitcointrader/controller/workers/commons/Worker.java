package com.evandro.challenges.bitsobitcointrader.controller.workers.commons;

import com.google.gson.Gson;
import lombok.Getter;
import lombok.Setter;

import java.util.Observable;

public class Worker extends Observable {

    protected Gson gson;

    protected String book;

    protected int size;

    @Getter
    @Setter
    protected boolean run;
}
