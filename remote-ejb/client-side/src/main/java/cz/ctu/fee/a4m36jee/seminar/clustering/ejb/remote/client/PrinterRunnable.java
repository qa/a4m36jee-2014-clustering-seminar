/*
 * JBoss, Home of Professional Open Source.
 * Copyright 2012, Red Hat, Inc., and individual contributors
 * as indicated by the @author tags. See the copyright.txt file in the
 * distribution for a full listing of individual contributors.
 *
 * This is free software; you can redistribute it and/or modify it
 * under the terms of the GNU Lesser General Public License as
 * published by the Free Software Foundation; either version 2.1 of
 * the License, or (at your option) any later version.
 *
 * This software is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this software; if not, write to the Free
 * Software Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA
 * 02110-1301 USA, or see the FSF site: http://www.fsf.org.
 */
package cz.ctu.fee.a4m36jee.seminar.clustering.ejb.remote.client;

import java.util.Collections;
import java.util.Map;
import java.util.concurrent.Semaphore;

/**
 * @author Ondrej Chaloupka
 */
public class PrinterRunnable implements Runnable {

    private Map<String, Integer> calls;
    private Semaphore printingSemaphore;

    public PrinterRunnable(Map<String, Integer> calls, Semaphore printingSemaphore) {
        this.calls = calls;
        this.printingSemaphore = printingSemaphore;
    }

    @Override
    public void run() {
        while (!Thread.interrupted()) {
            // sleep
            try {
                Thread.sleep(300);
            } catch (InterruptedException e) {
                return; //we were interrupted - finishing our job
            }

            // we don't want to other thread changing data
            Map<String, Integer> copiedCalls = Collections.unmodifiableMap(calls);

            // count number of calls
            int sumOfCalls = copiedCalls.keySet()
                    .stream()
                    .map(node -> copiedCalls.get(node)).mapToInt(Integer::intValue)
                    .sum();

            // format string to print
            String out = "";
            for (String nodeName : copiedCalls.keySet()) {
                int nodeCalls = copiedCalls.get(nodeName);
                Double percents = (nodeCalls * 1.0 / sumOfCalls) * 100;
                out += String.format("[%s: %1.2f %% (%d)]", nodeName, percents, nodeCalls);
            }

            try {
                printingSemaphore.acquire();
                System.out.println(out);
                printingSemaphore.release();
            } catch (InterruptedException ie) {
                return; //we were interrupted - finishing our job
            }
        }
    }
}
