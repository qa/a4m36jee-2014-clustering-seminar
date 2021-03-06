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

import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Semaphore;

import javax.naming.NamingException;

import cz.ctu.fee.a4m36jee.seminar.clustering.ejb.remote.stateless.StatelessRemote;

/**
 * @author Ondrej Chaloupka
 */
public class StatelessRemoteClient {

    public static void main(String[] args) throws Exception {
        StatelessRemote statelessBean = lookupStatelessRemote();
        int iterations = args.length > 0 ? Integer.parseInt(args[0]) : 100;

        String input = "";
        BufferedReader br = new BufferedReader(new InputStreamReader(System.in));

        Semaphore printingSemaphore = new Semaphore(1);
        Map<String, Integer> numberOfCalls = new HashMap<>();

        ExecutorService executor = Executors.newSingleThreadExecutor();
        Runnable worker = new PrinterRunnable(numberOfCalls, printingSemaphore);
        executor.execute(worker);

        while (!"q".equals(input.trim())) {

            // TODO: call stateless bean method and watch the load balancing

            printingSemaphore.acquire();
            System.out.print("To exit enter 'q', to continue hit ENTER: ");
            input = br.readLine();
            printingSemaphore.release();
        }

        executor.shutdownNow(); // calling interrupt()
        // Wait for executors to finish
        while (!executor.isTerminated()) {}
    }

    /**
     * Do remote lookup.
     *
     * @see https://docs.jboss.org/author/display/WFLY8/EJB+invocations+from+a+remote+client+using+JNDI
     */
    private static StatelessRemote lookupStatelessRemote() throws NamingException {
        // TODO: setup properties

        // TODO: jndi lookup for stateless bean

        return null;
    }
}
