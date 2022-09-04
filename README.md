It has taken quite a bit more effort to meet the forex challenge again, but this time using the provided framework and Scala as the implementation language, since dealing with:
New language
New IDE  (Intelli J)
New code base and frameworks
However, most requirements have been met, with Test Specs. The summary of which is uploaded to forex-mtl/ScalaTests_in_'scala'.html

Req:
[1] The service returns an exchange rate when provided with 2 supported currencies 
[2] The rate should not be older than 5 minutes
[3] The service should support at least 10,000 successful requests per day with 1 API token

Req Satus:
[1] DONE 
[2] DONE but code set to 1 min for testing (config would be better)
[3] 1/2 DONE 10,000 requests for rates can be made but no API token check has been added.
[*] DONE one extra service /currencies to list all currencies 

The testing is split into four directories:
[1] test => which can be run standalone i.e. without server support
[2] test-integration => run in concert with OneFrame and forex-mtl 
[3] test-manual => test that need manual intervention
[4] tes-poc => test playground with a series of worksheets for experimenting 

OneFrame has been left running on port 8080
forex-mtl config has been changed to run on 8081  (application.conf)

The bulk of the new code is in the forex/services/oneframe directory:

│  └── scala
│    └── forex
│      └── services
│        ├── oneframe
│        │  ├── OneFrameRateConsumer.scala
│        │  ├── OneFrameRateStreamReader.scala
│        │  ├── OneFrameRates.scala
│        │  └── OneFrameService.scala

The central file is OneFrameRates which is both the store of rates and responsible for orchestrating both /rates and /streaming/rates requests.

To assist with the solution the package http4sClient was added. However, my prototyping failed and I fell back on snatching and extending something straight forwards that I called forex/http/HttpVerySimple .sc

In the spirit of the forex-prox design being more than one API implementation, I added next to the  OneFrameDummy provider, a BloombergDummy and a ReutersDummy. This led me to think about the "to share or not to share"  rates stored between different suppliers. For time pressure's sake, I chose "not to share" the rate store but it could have been done.

This time around I did not attempt any optimisation with the number of rates each /streaming/rates request is making. They are in fact, each stuck with just receiving rates from one pair, when they could of being handling more, as the implementation and testing are complete.

Instead of the stream connection optimisation buckets of my Java solution, I added more thought to and implementation of error handling, starting with a full decode of OneFrame responses seen and finishing with mappings to exceptions into two subgroups:
ErrorInProvisionOfService - e.g. ErrorRateStale 
ErrorInUsageOfService - e.g. ErrorWithCurrencyPairGiven

Thank you very much for the challenge. From both of them, I have learned a lot, hence I am looking forwards to sharing some more of that and answering any further questions you may have.
