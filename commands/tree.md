# tree

tree 源码下载地址：http://mama.indstate.edu/users/ice/tree/src/tree-1.8.0.tgz ，通过 wget 进行下载。

下载之后，修改文件夹中的 Makefile 文件，例如在 Mac 的 OS X 系统下，将 
```makefile
# Linux defaults:
CFLAGS=-ggdb -pedantic -Wall -DLINUX -D_LARGEFILE64_SOURCE -D_FILE_OFFSET_BITS=64
CFLAGS=-O4 -Wall  -DLINUX -D_LARGEFILE64_SOURCE -D_FILE_OFFSET_BITS=64
LDFLAGS=-s
```
注释掉，打开：
```makefile
# Uncomment for OS X:
# It is not allowed to install to /usr/bin on OS X any longer (SIP):
prefix = /usr/local
CC=cc
CFLAGS=-O2 -Wall -fomit-frame-pointer -no-cpp-precomp
LDFLAGS=
MANDIR=/usr/share/man/man1
OBJS+=strverscmp.o
```
执行 `make` 命令，即可生成 tree 可执行文件。

