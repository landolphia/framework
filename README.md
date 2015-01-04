framework
=========

A Java game loop.

#Quickstart

Compile with
<pre><code>
javac -cp ../jar/minim.jar:../jar/disruptor.jar:../jar/lwjgl.jar:../j    ar/lwjgl_util.jar: -d . *.java
</code></pre>

Run with
<pre><code>
java -classpath ../jar/jl1.0.jar:../jar/mp3spi1.9.4.jar:../jar/triton    us_share.jar:../jar/jsminim.jar:../jar/minim.jar:../jar/disruptor.jar:../jar/lwj    gl.jar:../jar/lwjgl_util.jar: -Djava.library.path=../native/linux/x64/ framework    .GameLoop
</code></pre>

note: you need to have the lwjgl3 jars in ../jar and the native in ../native

tree ../jar
<pre><code>
../jar/
├── disruptor.jar
├── jl1.0.jar
├── jsminim.jar
├── lwjgl.jar
├── lwjgl_util.jar
├── minim.jar
├── mp3spi1.9.4.jar
└── tritonus_share.jar
</code></pre>

tree ../native
<pre><code>
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
