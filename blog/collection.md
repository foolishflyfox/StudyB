# java 中常用的数据容器

## Set

- TreeSet: 支持按范围查找(subSet)，遍历时有序; 非线程安全；
- HashSet: 查找效率高 O(1)，遍历时无序；非线程安全；
- LinkedHashSet：遍历时按元素添加顺序返回；非线程安全；
- CopyOnWriteArraySet: 线程安全的 set
