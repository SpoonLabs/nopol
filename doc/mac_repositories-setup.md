### Java Setup:

Downloaded and installed Java v1.7.0_55 from page for Mac OS X 64 bits.

	$ java -version 
	java version "1.7.0_55" 
	Java(TM) SE Runtime Environment (build 1.7.0_55-b13) 
	Java HotSpot(TM) 64-Bit Server VM (build 24.55-b03, mixed mode)

### My ssh key:

	$ ssh-keygen -t rsa -C "srlm"

	id_rsa.pub

	ssh-rsa AAAAB3NzaC1yc2EAAAADAQABAAABAQCsv6grnnM2iNrJgOwihu6y3xLsJJag6XPw4QePDT14TStkuvuDR2s+stxH1yAV/MCr0k3wcMkCYF2yL/8+o9sGTOwGszKPvM
	+pgOr8GnK8cwcyKibhG9QTzQoFBqtn6N6XyZqGosWWi+9uvFC2TiOrBK3c2zUmYatCAbajTTe80Ky4f6UvYO7UFhs0FUzpo9gp4fddy6OIIhwzouT/HYzsf5u4rkcyBxv/FG
	+8b1PZYZQnpHrADb1VJQFLb2wtct22JLLemTWk0sVpNys/YL1CWeIRdtY3HCPhDpzTnK5xp6xLZHutpUZYurP/W30LLak1LmiuMSvEd+NujmmPh/KV srlm

Paste this key in the following link: <http://gforge.inria.fr/account/editsshkeys.php>.


### Setup for Sacha:

	$ git clone git+ssh://srlm@scm.gforge.inria.fr//gitroot/sachaproject/sacha-infrastructure.git

With this constants for the terminal:

	# in the file ~/.bash_profile
	export JAVA_HOME=$(/usr/libexec/java_home)
	export M2_HOME=/Users/virtual/Desktop/data/programs/apache-maven-3.2.1
	export PATH=$PATH:$M2_HOME/bin

	$ cd sacha-infrastructure

	$ mvn install 


### Setup for Spoon:

	$ svn checkout  svn+ssh://srlm@scm.gforge.inria.fr/svnroot/spoon/trunk/spoon-core

With this constants for the terminal: _(same as above.)_

The log for the revision says the following:

	------------------------------------------------------------------------ 
	r2383 | martin | 2014-04-02 21:59:27 +0200 (Wed, 02 Apr 2014) | 1 line
	[maven-release-plugin] prepare release spoon-core-2.0

	$ svn up -r r2383

	$ mvn install


### Setup for JSMT library:

	$ git clone git+ssh://srlm@scm.gforge.inria.fr//gitroot/sachaproject/jsmtlib.git

With this constants for the terminal: _(same as above.)_

	$ cd jsmtlib

	$ mvn install 


### Setup for Nopol:

	$ git clone git+ssh://srlm@scm.gforge.inria.fr//gitroot/sachaproject/nopol.git

	$ mvn install

In the directory `nopol/nopol/lib` there should be three files: `jSMTLIB.jar`, spoon's most recent jar (in my case, `spoon-core-2.0.jar`) and gzoltar's (in my case, `com.gzoltar-0.0.4-jar-with-dependencies.jar`). For the latter, the `pom.xml` file should be updated:

	<dependency>
		<groupId>com.gzoltar</groupId>
		<artifactId>gzoltar</artifactId>
		<version>0.0.4</version>
		<scope>system</scope>
		<systemPath>${project.build.sourceDirectory}/../../../lib/com.gzoltar-0.0.4-jar-with-dependencies.jar</systemPath>
	</dependency>

Lastly, given that there were compatibility problems using Z3 SMT Solver on MacOSX, I downloaded and installed CVC4 (`cvc4-1.3_4.MacOs85.MountainLion.mpkg`) from the page <http://cvc4.cs.nyu.edu/cvc4-builds/macos/>. In order to use this solver, it is mandatory to update the file `SolverFactory.java`:

	public final class SolverFactory {
	
		private static final String CVC4_BINARY_PATH = "/opt/local/bin/cvc4";
		private final Configuration smtConfig;
	
		public SolverFactory(final Configuration smtConfig) {
			this.smtConfig = smtConfig;
		}
	
		public ISolver create() {
			FileHandler.ensurePathIsValid(CVC4_BINARY_PATH);
			return new Solver_cvc4(smtConfig, CVC4_BINARY_PATH);
		}
	}


