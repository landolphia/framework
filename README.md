framework
=========

A Java game loop.

#Quickstart

Compile with
```javac -cp ../jar/disruptor.jar:../jar/lwjgl.jar:../jar/lwjgl_util.jar: -d . *.java```

Run with
```java -classpath ../jar/disruptor.jar:../jar/lwjgl.jar:../jar/lwjgl_util.jar: -Djava.library.path=../native/linux/x64/ framework.GameLoop```

note: you need to have the lwjgl3 jars in ../jar and the native in ../native

tree ../jar
```../jar/
├── disruptor.jar
├── lwjgl.jar
└── lwjgl_util.jar```

tree ../native
```../native/
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
			        └── OpenAL32.dll```



#Resources, libraries, references

Jason L. McKesson (arcsynthesis.org)
