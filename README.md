# Learn you a language
Simple web app created using FP principles in Scala.
Check out the details about libraries used in build.sbt

The app itself is designed to support the process of learning the foreign language.

One can build docker image with this app (based on alpine) with the following sbt command:  
`sbt docker:publishLocal`

If you wish to run minimal postgres working with this app in default setting you can run:  
`./run-postgres.sh`

The above command starts docker container with postgres underneath.


Have a nice day!  
_Paweł_
