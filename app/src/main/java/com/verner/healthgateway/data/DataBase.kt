//import io.appwrite.Client
//import io.appwrite.services.Databases
//import io.appwrite.models.Database
//import io.appwrite.models.Collection
//import kotlinx.coroutines.coroutineScope
//
//class Database(private val client: Client) {
//
//    private val databasesService by lazy { Databases(client) }
//
//    suspend fun createCollection(name: String): Collection {
//        return coroutineScope {
//            databasesService.createCollection(name = name)
//        }
//    }
//}