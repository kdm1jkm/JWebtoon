[![](https://jitpack.io/v/kdm1jkm/JWebtoon.svg)](https://jitpack.io/#kdm1jkm/JWebtoon)

---

gradle
```groovy
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

```groovy
dependencies {
    implementation 'com.github.kdm1jkm:JWebtoon:Tag'
}
```

---

maven
```xml
<repositories>
    <repository>
        <id>jitpack.io</id>
        <url>https://jitpack.io</url>
    </repository>
</repositories>
```

```xml
<dependency>
    <groupId>com.github.kdm1jkm</groupId>
    <artifactId>JWebtoon</artifactId>
    <version>Tag</version>
</dependency>
```