package com.silicon.cure_sync

import android.util.Log
import com.google.firebase.firestore.FirebaseFirestore

data class Medicine(
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val ageGroup: String = "",
    val imageUrls: List<Int> = listOf()  // Multiple image URLs
)

data class MedicalInstrument(
    val name: String = "",
    val description: String = "",
    val price: Double = 0.0,
    val usage: String = "",
    val imageUrls: List<String> = listOf() // Multiple image URLs
)

fun createCollections() {
    val db = FirebaseFirestore.getInstance()

    // Helper function to create nested collections
    fun <T : Any> createSubCollection(
        parentCollection: String,
        parentDocument: String,
        subCollection: String,
        items: List<T>
    ) {
        val subCollectionRef = db.collection(parentCollection).document(parentDocument).collection(subCollection)

        items.forEach { item ->
            subCollectionRef.add(item)
                .addOnSuccessListener {
                    Log.d("Firestore", "Successfully added item: $item")
                }
                .addOnFailureListener { e ->
                    Log.e("FirestoreError", "Error adding item: $item", e)
                }
        }
    }

    // Medicines Collections (Ointments, Tablets, Syrups)
    createSubCollection(
        parentCollection = "medicines",
        parentDocument = "ointments",
        subCollection = "items",
        items = listOf(
            Medicine("Hydrocortisone Cream", "Used to treat skin inflammation and itching.", 5.99, "Adults",
               imageUrls = listOf(R.drawable.paracetamol_tablet1,R.drawable.paracetamol_tablets2 )),
            Medicine("Neosporin", "Antibiotic ointment to prevent infections in minor cuts.", 7.99, "Children",
                imageUrls = listOf(R.drawable.paracetamol_tablet1,R.drawable.paracetamol_tablets2 )),
            Medicine("Bacitracin", "Antibiotic ointment used to prevent bacterial infections.", 6.49, "Adults",
                imageUrls = listOf(R.drawable.paracetamol_tablet1,R.drawable.paracetamol_tablets2 )),
            Medicine("Calamine Lotion", "Used to treat itching and skin irritation.", 4.99, "All Ages",
                imageUrls = listOf(R.drawable.paracetamol_tablet1,R.drawable.paracetamol_tablets2 )),
            Medicine("Mupirocin", "Used to treat bacterial skin infections.", 8.49, "Adults",
                imageUrls = listOf(R.drawable.paracetamol_tablet1,R.drawable.paracetamol_tablets2 ))
        )
    )

    createSubCollection(
        parentCollection = "medicines",
        parentDocument = "tablets",
        subCollection = "items",
        items = listOf(
            Medicine("Paracetamol", "Used to treat pain and fever.", 2.99, "All Ages",
                imageUrls = listOf(R.drawable.paracetamol_tablet1,R.drawable.paracetamol_tablets2 )),
            Medicine("Ibuprofen", "Anti-inflammatory used to treat pain and fever.", 3.49, "Adults",
                imageUrls = listOf(R.drawable.ibuprofen_tablets1,R.drawable.ibuprofen_tablets2 )),
            Medicine("Aspirin", "Used to reduce pain, fever, or inflammation.", 3.99, "Adults",
                imageUrls = listOf(R.drawable.aspirin_tablet1,R.drawable.aspirin_tablet2 )),
            Medicine("Amoxicillin", "Antibiotic used to treat bacterial infections.", 6.99, "Children",
                imageUrls = listOf(R.drawable.amoxicillin_tablets1,R.drawable.amoxicillin_tablets2 )),
            Medicine("Loratadine", "Antihistamine used to treat allergies.", 4.99, "All Ages",
                imageUrls = listOf(R.drawable.loratadine_tablet1,R.drawable.loratadine_tablets2 ))
        )
    )

    createSubCollection(
        parentCollection = "medicines",
        parentDocument = "syrups",
        subCollection = "items",
        items = listOf(
            Medicine("Cough Syrup", "Used to relieve coughing.", 4.99, "Children",
                imageUrls = listOf(R.drawable.paracetamol_tablet1,R.drawable.paracetamol_tablets2 )),
            Medicine("Vitamin C Syrup", "Boosts immune system and overall health.", 7.49, "All Ages",
                imageUrls = listOf(R.drawable.paracetamol_tablet1,R.drawable.paracetamol_tablets2 )),
            Medicine("Iron Supplement Syrup", "Used to treat or prevent low blood levels of iron.", 6.99, "Adults",
                imageUrls = listOf(R.drawable.paracetamol_tablet1,R.drawable.paracetamol_tablets2 )),
            Medicine("Antihistamine Syrup", "Used to relieve allergy symptoms.", 5.99, "Children",
                imageUrls = listOf(R.drawable.paracetamol_tablet1,R.drawable.paracetamol_tablets2 )),
            Medicine("Liver Tonic", "Supports liver health and function.", 8.49, "Adults",
                imageUrls = listOf(R.drawable.paracetamol_tablet1,R.drawable.paracetamol_tablets2 ))
        )
    )

    // Medical Instruments Collection
    createSubCollection(
        parentCollection = "medicalInstruments",
        parentDocument = "instruments",
        subCollection = "items",
        items = listOf(
            MedicalInstrument("Digital Thermometer", "Used to measure body temperature.", 12.99, "Home and Clinical",
                imageUrls = listOf("https://your_storage_url/digital_thermometer_1.jpg", "https://your_storage_url/digital_thermometer_2.jpg")),
            MedicalInstrument("Blood Pressure Monitor", "Used to measure blood pressure levels.", 29.99, "Home and Clinical",
                imageUrls = listOf("https://your_storage_url/blood_pressure_monitor_1.jpg", "https://your_storage_url/blood_pressure_monitor_2.jpg")),
            MedicalInstrument("Stethoscope", "Used to listen to heart and lung sounds.", 49.99, "Clinical",
                imageUrls = listOf("https://your_storage_url/stethoscope_1.jpg", "https://your_storage_url/stethoscope_2.jpg")),
            MedicalInstrument("Glucometer", "Used to measure blood glucose levels.", 25.99, "Home and Clinical",
                imageUrls = listOf("https://your_storage_url/glucometer_1.jpg", "https://your_storage_url/glucometer_2.jpg")),
            MedicalInstrument("Pulse Oximeter", "Used to measure oxygen saturation levels.", 19.99, "Home and Clinical",
                imageUrls = listOf("https://your_storage_url/pulse_oximeter_1.jpg", "https://your_storage_url/pulse_oximeter_2.jpg"))
        )
    )
}
