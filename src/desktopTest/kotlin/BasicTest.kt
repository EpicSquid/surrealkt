import dev.epicsquid.surrealkt.connection.SurrealWebsocketConnection
import dev.epicsquid.surrealkt.driver.SurrealDriver
import dev.epicsquid.surrealkt.driver.model.RootAuth
import kotlinx.coroutines.runBlocking
import kotlinx.serialization.Serializable
import org.junit.jupiter.api.Assertions
import org.junit.jupiter.api.Test


class BasicTest {

    @Serializable
    data class User(val id: String? = null, val username: String, val password: String)

    @Test
    fun pingTest() {
        runBlocking {
            val driver = SurrealDriver(SurrealWebsocketConnection("localhost").apply { runBlocking { connect() } })
            Assertions.assertTrue(driver.ping())
        }
    }

    @Test
    fun createTest(){
        runBlocking {
            val driver = SurrealDriver(SurrealWebsocketConnection("localhost").apply { runBlocking { connect() } })
            driver.signIn(RootAuth(
                user = "root",
                pass = "root",
            ))
            driver.use("test", "test")
            driver.query<User>("delete user_table;")
            val user = driver.create<User, User>("user_table:test_user", User(username = "James", password = "Password123"))
            Assertions.assertEquals("James", user.username)
            Assertions.assertEquals("Password123", user.password)
        }
    }

    @Test
    fun changeTest(){
        runBlocking {
            val driver = SurrealDriver(SurrealWebsocketConnection("localhost").apply { runBlocking { connect() } })
            driver.signIn(RootAuth(
                user = "root",
                pass = "root",
            ))
            driver.use("test", "test")
            driver.query<User>("delete user_table;")
            driver.create<User, User>("user_table:test_user", User(username = "James", password = "Password123"))
            val user = driver.change<User, User>("user_table:test_user", User(username = "James1", password = "Password123!"))
            val returned = user.first()
            Assertions.assertEquals("James1", returned.username)
            Assertions.assertEquals("Password123!", returned.password)
        }
    }

    @Test
    fun selectTest(){
        runBlocking {
            val driver = SurrealDriver(SurrealWebsocketConnection("localhost").apply { runBlocking { connect() } })
            driver.signIn(RootAuth(
                user = "root",
                pass = "root",
            ))
            driver.use("test", "test")
            driver.query<User>("delete user_table;")
            driver.create<User, User>("user_table:test_user", User(username = "James", password = "Password123"))
            val user = driver.select<User>("user_table:test_user")
            val returned = user.first()
            Assertions.assertEquals("James", returned.username)
            Assertions.assertEquals("Password123", returned.password)
        }
    }
}