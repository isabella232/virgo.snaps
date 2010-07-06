SpringSource Snaps Multiple Styles Sample
------------------------------------------

This sample illustrates how multiple Snaps can be deployed together to provide a choice of 
styles to a host. It uses some simple JavaScript to allow the user to select which style they
wish to use.

The sample is made up of three war projects:

 - multi-style-host
 - green-style-snap
 - plain-style-snap

Each project can be build by moving into its directory and running ant jar. This will result in a
.war file being created in the projects target/artifacts directory.

Once the Host and the two Snaps have been built. The Host and then both of its Snaps should be
deployed to dm Server. The sample can then be accessed at http://localhost:8080/multiple-styles

Before deploying the Host and its Snaps to dm Server the Snaps subsystem must have been
installed. Please refer to 
http://blog.springsource.com/2009/06/22/modular-web-applications-with-springsource-snaps for
details. 
