framework
=========

An unfinished Java game loop.

#Quickstart

Compile with
<code><pre>
javac -cp ../jar/disruptor.jar:../jar/lwjgl.jar:../jar/lwjgl_util.jar: -d . *.java
</code></pre>

Run with
<code><pre>
java -classpath ../jar/disruptor.jar:../jar/lwjgl.jar:../jar/lwjgl_util.jar: -Djava.library.path=../native/linux/x64/ framework.GameLoop
</code></pre>

note: you need to have the lwjgl3 jars in ../jar and the native in ../native

tree ../jar
<code><pre>
../jar/
├── disruptor.jar
├── lwjgl.jar
└── lwjgl_util.jar
</code></pre>

tree ../native
<code><pre>
../native/
├── linux
│   ├── x64
│   │   ├── liblwjgl.so
│   │   └── libopenal.so
│   └── x86
│       ├── liblwjgl.so
│       └── libopenal.so
├── macosx
│   └── x64
│       ├── liblwjgl.dylib
│       └── libopenal.dylib
└── windows
    ├── x64
        │   ├── lwjgl.dll
	    │   └── OpenAL32.dll
	        └── x86
		        ├── lwjgl.dll
			        └── OpenAL32.dll
</code></pre>



Resources, libraries, references

Jason L. McKesson (arcsynthesis.org)
