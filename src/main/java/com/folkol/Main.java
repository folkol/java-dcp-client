package com.folkol;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.Invocation;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

public class Main {
    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Conf {
        @JsonIgnoreProperties(ignoreUnknown = true)
        static class Node {
            public Map<String, Integer> ports;
            public String hostname;
        }

        public List<Node> nodes;
    }

    @JsonIgnoreProperties(ignoreUnknown = true)
    static class Bucket {
        @JsonIgnoreProperties(ignoreUnknown = true)
        static class ServerMap {
            public String hashAlgorithm;
            public int numReplicas;
            public List<String> serverList;
            public List<List<Integer>> vBucketMap;
        }

        public String name, saslPassword;
        public ServerMap vBucketServerMap;

        @Override
        public String toString() {
            return String.format("%s %s %s", name, saslPassword, vBucketServerMap.vBucketMap);
        }
    }

    static class BucketList extends ArrayList<Bucket> {
    }

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient()
//                .register(new JacksonJaxbJsonProvider().configure(FAIL_ON_UNKNOWN_PROPERTIES, false))
                .register(HttpAuthenticationFeature.basic("admin", "password"));

        Invocation.Builder pools = client.target("http://localhost:8091/pools/default").request();
        Conf conf = pools.get(Conf.class);
        for (Conf.Node n : conf.nodes)
            System.out.println(n.ports);

        Invocation.Builder buckets = client.target("http://localhost:8091/pools/default/buckets").request();
        System.out.println(buckets.get(String.class));
        Bucket cmBucket = buckets.get(BucketList.class).get(0);
        System.out.println(cmBucket.vBucketServerMap.serverList);

//        new Socket(cmBucket.name)
    }
}
