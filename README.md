## Setup
- I recommend using Java 8, but I don't know if it will work with newer Java versions (since the server is running Java 8 i think)
- I have no idea how I did it but you can find the exact craftbukkit jar of the server here too. Maven can somehow generate the API from that and make the .jar link against it, but tbh don't ask me how. Might explain it later if I figure out how to.
- Run `mvn clean install`
- To package, run `mvn package`. The .jar you can copy to the `plugins` folder of the server is in `target/luvstar-plugin-1.0-SNAPSHOT.jar`