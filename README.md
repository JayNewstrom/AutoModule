AutoModule
=========

AutoModule is a simple [Dagger][dagger] [Module][daggerModule] generator. 
It specifies the `injects` as the class `@AutoModule` is on, and allows specifying a module the generated module `addsTo`.

```java
@AutoModule(addsTo = ApplicationModule.class)
public final class MainActivity extends Activity {

    @Inject String test;

    @Override protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        ObjectGraph rootGraph = ObjectGraph.create(new ApplicationModule());
        ObjectGraph childGraph = rootGraph.plus(getDaggerModule());
        childGraph.inject(this);
        setContentView(R.layout.activity_main);
        ((TextView) findViewById(R.id.text_view)).setText(test);
    }

    protected Object getDaggerModule() {
        return new MainActivityModule();
    }
}
```

`@AutoModule` on `MainActivity` generates a class that looks similar to what's shown below.

```java
@Module(
    injects = MainActivity.class,
    addsTo = ApplicationModule.class
)
final class MainActivityModule {
}
```

License
-------

    Copyright 2015 Jay Newstrom

    Licensed under the Apache License, Version 2.0 (the "License");
    you may not use this file except in compliance with the License.
    You may obtain a copy of the License at

       http://www.apache.org/licenses/LICENSE-2.0

    Unless required by applicable law or agreed to in writing, software
    distributed under the License is distributed on an "AS IS" BASIS,
    WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
    See the License for the specific language governing permissions and
    limitations under the License.

[dagger]: https://github.com/square/dagger
[daggerModule]: https://github.com/square/dagger/blob/master/core/src/main/java/dagger/Module.java
