DCP is memcached protocul, with additional OP Codes.
	- And vBucket instead of Ketama (http://www.audioscrobbler.net/development/ketama)
	- 




* Simple client: https://github.com/couchbaselabs/dcp-documentation/blob/master/documentation/building-a-simple-client.md
* https://github.com/couchbaselabs/python-dcp-client.git
* http://docs.couchbase.com/developer/dev-guide-3.0/client-library.html
* 






* DCP kräver Couchbase 3.0
* DCP also provides fine grained restartability in order to allow consumers of DCP to resume from exactly where they left off no matter how long they have been disconnected.
* The documentation contained on this website is written mainly for those interested in learning the in depth details of how the DCP protocol works in Couchbase or for developers who wish to build their own DCP Consumers to connect their third-party components to Couchbase.
* Architectural concepts: Order (within vbucket…), Restartability, Consistency (snapshots), Performance (RAM -> RAM)
* Terminology:
	- DCP Connection: TCP connection over which DCP is used
	- DCP Stream: A sequence of data mutations for a given VBucket
	- Consumer: An endpoint that is consuming a DCP Stream
	- History branch (as in a branch of mutations history -> new VBucket id in failover log)
	- Failover Log: A log of all possible history branches
	- Failover Log Entry: VBucket UUID + highest assigned sequence number
	- Mutation: Delete key or mutate data pointed to by key
	- Producer: An endpoint producing a DCP Stream
	- Rollback: Delete all documents received after sequence number
	- Sequence Number: Strictly increasing (within VBucket with same Failover Log Entry) mutation id
	- Snapshot: A batch of sequence numbers send in a DCP Stream
	- VBucket one (of 1024) shards within a bucket.
	- VBucket UUID: A UUID denoting a history branch
* DCP = Memcached protocol with additional OPCodes and full duplex: https://code.google.com/p/memcached/wiki/BinaryProtocolRevamped
* Typical scenario: Client connects and requests DCP Stream, Server starts to send Stream. During transmission, Client can send more commands.
* Example protocol flow:
	1) Consumer opens connection to Producer
	2) Consumer sends “Open ConnectionÄ message
	3) Producer responds with an “Ok Message”
	(4) Consumer sends some “Control Messages”, unless defaults are ok)
	5) Consumer sends “Stream Request” to Producer (https://github.com/couchbaselabs/dcp-documentation/blob/master/documentation/commands/stream-request.md)
		- Specifying from where to resume, or 0 for full history
	6) The Producer replies with either “Ok Message” or “Rollback Message”
		- If Rollback: (Remove data and) send new “Stream Request”
		- If Ok: Persist failoverlog from Ok message, and start consuming messages
	7) Stream:
		1) Snapshot marker
		2) A series of “Mutation”, “Deletion” and “Expiration” messages that are part of the current snapshot
		3) “Snapshot marker” or “Stream End”
			- If Snapshot Marker, jump to 2.
			- If Stream End,
* Dead Connection Detection: Keep-alive by “No-op”. (Turned off by default)
* Should not start one connection per VBucket
*






* ElasticSearch / LucidWorks Solr-Couchbase-Plugin is using the 2.0 XDCR feature… “Acts like” a couchbase cluster, and the main cluster sets up and starts replication.
* Det verkar som att man kan börja om från början, även efter “ta bort document och restart couchbase”. Man verkar dock bara få “deleted”, inte “created”
*



TAP, internal Cluster data transfer.








Previous XDCR used CAPI mode XDCR (for example, by the ElasticSearch plugin) which batched mutations and sent them using the REST API called CAPI (unpacked on the destination, and integrated with individual memcached OPs.)
Couchbase 2.2 and later can use XMEM (XDCR protocol 2).
Couchbase 3.0 and later can use DCP (and will use DCP for bucket streams, even within the cluster).








Couchbase XDCR for indexing

DCP is the new XDCR-protocol used by Couchbase 3.


DCP recommended by Couchbase devs (Mike W) on #couchbase @ Freenode.






Old XDCR pushed changes to remote CB Cluster. To be able to use this for indexing, the indexer will have to emulate a Couchbase Cluster (this is used by the ElasticSearch Couchbase connector, and the Solr Couchbase plugin.)

CDP / TAP









References:
http://www.slideshare.net/Couchbase/dcp-deep-dive-couchbase-connect-2014-mikewcihanb
http://docs.couchbase.com/developer/dev-guide-3.0/client-library.html
