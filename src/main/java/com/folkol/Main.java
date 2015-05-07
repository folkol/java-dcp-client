package com.folkol;

import org.codehaus.jackson.jaxrs.JacksonJaxbJsonProvider;
import org.codehaus.jackson.type.TypeReference;
import org.glassfish.jersey.client.authentication.HttpAuthenticationFeature;

import javax.ws.rs.client.Client;
import javax.ws.rs.client.ClientBuilder;
import javax.ws.rs.client.WebTarget;
import java.util.ArrayList;
import java.util.List;

import static org.codehaus.jackson.map.DeserializationConfig.Feature.FAIL_ON_UNKNOWN_PROPERTIES;

public class Main {
    static class Conf {
        static class Node {
            public String hostname;
        }

        public List<Node> nodes;
    }
    static class Bucket {

    }
    static class BucketList extends ArrayList<Bucket> {

    }

    public static void main(String[] args) {
        Client client = ClientBuilder.newClient()
                .register(new JacksonJaxbJsonProvider().configure(FAIL_ON_UNKNOWN_PROPERTIES, false))
                .register(HttpAuthenticationFeature.basic("admin", "password"));

        WebTarget pools = client.target("http://localhost:8091/pools/default");
        System.out.println(pools.request().get(String.class));
        Conf conf = pools.request().get(Conf.class);
        for (Conf.Node n : conf.nodes)
            System.out.println(n.hostname);

        WebTarget buckets = client.target("http://localhost:8091/pools/default/buckets");
        System.out.println(buckets.request().get(BucketList.class));
    }
}
