# git 补丁的创建与使用

## 初始化实验内容

执行如下的命令：
```shell
mkdir patch-test
cd patch-test
git init
echo "master first line" > README.md
git add README.md
git commit -m "1 commit"

# 创建分支 A 并进行一系列操作
git checkout -b branchA
echo "aaaa" >> README.md
git commit -am "append aaaa"
echo "bbb" >> README.md
git commit -am "append bbb"
echo "cc" >> README.md
git commit -am "append cc"
grep -v "bbb" README.md > t.md && rm README.md && mv t.md README.md
git commit -am "remove bbb"
```
通过 `git log` 可以看到 branchA 分支有如下的提交：
```
417e2b76ca382049fd2034365ce3f3fb228cb4f9 remove bbb
f9cb900d41a3fceb984219b9b7a1e9a115f01c1a append cc
72855b0b439c12e05a40259f6a56cbe8e73610b5 append bbb
77622557e61d421f36f2b08fb1fdd7c6f05c84db append aaaa
c7a0be588d96400b87b3e5b40daa18380ec501a4 1 commit
```

## format-patch/diff & apply

### git format-patch 创建打包文件

#### 生成从指定 commit 之后的所有补丁

执行 `git format-patch commit-id` 可以为 commit-id 后的每个 commit 生成一个 patch 文件。patch 文件按照 commit 的先后顺序从1开始编号，不包括 commit-d 的 patch 文件。patch 文件会生成到当前目录下。例如，执行 `git format-patch 72855b0b`，将生成 0001-append-cc.patch、0002-remove-bbb.patch 两个文件。0001-append-cc.patch 的 内容为：
```
From f9cb900d41a3fceb984219b9b7a1e9a115f01c1a Mon Sep 17 00:00:00 2001
From: foolishflyfox <fenghuabin1992@163.com>
Date: Tue, 15 Feb 2022 05:21:49 +0800
Subject: [PATCH 1/2] append cc

---
 README.md | 1 +
 1 file changed, 1 insertion(+)

diff --git a/README.md b/README.md
index 2824225..47ddf00 100644
--- a/README.md
+++ b/README.md
@@ -1,3 +1,4 @@
 master first line
 aaaa
 bbb
+cc
-- 
2.13.5 (Apple Git-94)
```
如果希望指定补丁文件的生成目录，可添加参数 `-o 文件夹`，例如 `git format-patch 72855b0b -o ../patches` 将在 `../patches` 文件夹下创建补丁。

#### 生成最后 n 个补丁

生成距离 head 最近的 n 个 patch，可以使用 `git format-patch -n`。例如，`git format-patch -1` 只会生成 `0001-remove-bbb.patch` 补丁文件。`git format-patch -10 -o ../patches2` 讲在 patch2 目录下生成 0001-1-commit.patch、0002-append-aaaa.patch、0003-append-bbb.patch、0004-append-cc.patch、0005-remove-bbb.patch 共 5 个补丁。

#### 生成两个 commit 之间的 patch

`git format-patch commit-id1..commit-id2` 可以生成从 commit-id1 到 commit-id2 的所有 patch 文件，不包括 commit-id1。例如：`git format-patch 72855b..417e2b -o ../patches3`，将在 `../patches3` 中生成 0001-append-cc.patch、0002-remove-bbb.patch 两个 patch 文件。

**注意：commit-id1 需要在 commit-id2 之前。**

#### 生成指定 commit 及之前的 n-1 个 commit 的补丁

`git format-patch commit-id -n`，例如 `git format-patch 72855b -2 -o ../patches4` 将在 `patches4` 文件夹下生成文件 0001-append-aaaa.patch、0002-append-bbb.patch。

#### 多次 commit 压缩到一个文件

`git diff commit-id-a commit-id-b`，导出两个 commit 之间的补丁，其中两个 commit-id 的顺序不重要，前一个 commit 的内容不包含。例如：`git diff 72855b 417e2b > ../patches5/patch1.diff`，patch1.diff 的内容为：
```
diff --git a/README.md b/README.md
index 2824225..82065ab 100644
--- a/README.md
+++ b/README.md
@@ -1,3 +1,3 @@
 master first line
 aaaa
-bbb
+cc
```
`git diff c7a0be 417e2b > ../patches5/patch2.diff`，patch2.diff 的内容为：
```
diff --git a/README.md b/README.md
index e293212..82065ab 100644
--- a/README.md
+++ b/README.md
@@ -1 +1,3 @@
 master first line
+aaaa
+cc
```

### git apply 应用打包文件

#### 检查 patch/diff 能否打入

`git apply --check path/to/xxx.patch` / `git apply --check patch/to/xxx.diff`。如果没有冲突，该指令没有输出。

例如，我们从 master 生成 branchB：`git checkout master && git checkout -b branchB`。

- 执行 `git apply --check ../patches2/0002-append-aaaa.patch`，没有输出，表示可以应用该补丁；
- 执行 `git apply --check ../patches5/patch2.diff`，没有输出，表示可以应用该补丁；
- 执行 `git apply --check ../patches2/0003-append-bbb.patch`，输出如下：
```
error: patch failed: README.md:1
error: README.md: patch does not apply
```
表示不能应用该补丁。
- 执行 `git apply --check ../patches5/patch1.diff`，输出同上。

#### 打入补丁

`git apply ../patches5/patch2.diff`，此时修改被应用到工作区，`git diff` 的结果为：
```
# diff --git a/README.md b/README.md
index e293212..82065ab 100644
--- a/README.md
+++ b/README.md
@@ -1 +1,3 @@
 master first line
+aaaa
+cc
```
修改并没有提交。

## 参考

- [使用 git format-patch 將指定 commit 的內容補丁到任意分支](https://ephrain.net/git-%E4%BD%BF%E7%94%A8-git-format-patch-%E5%B0%87%E6%8C%87%E5%AE%9A-commit-%E7%9A%84%E5%85%A7%E5%AE%B9%E8%A3%9C%E4%B8%81%E5%88%B0%E4%BB%BB%E6%84%8F%E5%88%86%E6%94%AF/)

