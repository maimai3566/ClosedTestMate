package com.rururi.closedtestmate.recruit.data

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.google.firebase.Timestamp
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.firestore.FirebaseFirestore
import kotlinx.coroutines.launch
import kotlinx.coroutines.tasks.await
import java.util.Date

//todo:このファイルを消す
//1回限りのマイグレーション　postedAt対応
suspend fun migrate(db: FirebaseFirestore){
    //全件を取得（件数が多い場合は別途対応を検討）
    val snap = db.collection("recruit").get().await()
    val docs = snap.documents

    //Longだけのものを取得
    val targets = docs.filter{ it.get("postedAt") is Long }

    if (targets.isEmpty()) {
        println("Nothing to migrate")
        return
    }

    println("migration:Found ${targets.size} docs to update.")

    //500件ずつパッチで更新
    targets.chunked(500).forEachIndexed { chunkIndex, chunk ->
        val batch = db.batch()
        chunk.forEach { doc ->
            val v = doc.get("postedAt") as Long
            val ts = Timestamp(Date(v))
            batch.update(doc.reference, "postedAt", ts)
            println("migration:Updating ${doc.id}")
        }
        batch.commit().await()
        println("migration:Chunk $chunkIndex done.")
    }
}

@Composable
fun MigrateScreen() {
    Column(
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center,
        modifier = Modifier.padding(24.dp)
    ) {
        val scope = rememberCoroutineScope()
        var running by rememberSaveable { mutableStateOf(false)}
        var message by rememberSaveable { mutableStateOf("")}

        LaunchedEffect(Unit) {
            val auth = FirebaseAuth.getInstance()
            if (auth.currentUser == null) {
                auth.signInAnonymously().await()
            }
        }
        Spacer(Modifier.size(120.dp))
        Button(
            onClick = {
                if (running) return@Button
                running = true
                message = "Migrating..."
                scope.launch {
                    try {
                        migrate(FirebaseFirestore.getInstance())
                        message = "Migration done."
                    } catch (e: Exception) {
                        message = "Migration failed: ${e.message}"
                    }
                    running = false
                }
            },
            enabled = !running
        ) {
            Text("Migrate")
        }
        Spacer(Modifier.size(12.dp))
        Text(message)
    }
}