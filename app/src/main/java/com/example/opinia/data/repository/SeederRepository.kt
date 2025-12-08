package com.example.opinia.data.repository

import android.util.Log
import com.example.opinia.data.model.CommentReview
import com.example.opinia.data.model.Course
import com.example.opinia.data.model.Department
import com.example.opinia.data.model.Faculty
import com.example.opinia.data.model.Instructor
import com.example.opinia.data.model.Student
import com.google.firebase.firestore.FirebaseFirestore
import javax.inject.Inject

// IMPORTANT THIS REPOSITORY ONLY FOR ADDING METADATA TO FIREBASE FIRESTORE DATABASE
// DO NOT USE IT IN YOUR APPLICATION
// IF YOU NEED TO ADD NEW METADATA PLEASE INFORM OTHERS

class SeederRepository @Inject constructor(
    private val instructorRepository: InstructorRepository,
    private val facultyDepartmentRepository: FacultyDepartmentRepository,
    private val courseRepository: CourseRepository,
    private val firestore: FirebaseFirestore
) {

    private val TAG = "SeederRepository"

    private fun createSeedComment(comment: CommentReview) {
        firestore.collection("comments_review")
            .document(comment.commentId)
            .set(comment)
            .addOnSuccessListener {
                Log.d(TAG, "Seed comment created: ${comment.commentId}")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to create seed comment", e)
            }
    }

    private fun createSeedStudent(student: Student) {
        firestore.collection("students")
            .document(student.studentId)
            .set(student)
            .addOnSuccessListener {
                Log.d(TAG, "Seed Student created")
            }
            .addOnFailureListener { e ->
                Log.e(TAG, "Failed to create Seed Student", e)
            }
    }

    suspend fun seedDatabase() {
        try {
            Log.d(TAG, "SEEDING STARTED...")

            // DUMMY STUDENT
            val dummyStudent = Student(
                "apyir8gC5dUZQowxfQYU4Advwm83",
                "kaan.akkok@std.yeditepe.edu.tr",
                "Kaan",
                "Akkök",
                "7th Semester",
                "fac_communication",
                "dept_visual_communication_design",
                "turuncu",
                listOf("vcd111", "vcd171"),
                listOf("dummyCommentId")
            )
            createSeedStudent(dummyStudent)

            // DUMMY COMMENT
            val dummyComment = CommentReview(
                "dummyCommentId",
                "vcd111",
                "apyir8gC5dUZQowxfQYU4Advwm83",
                3,
                "This is the best course. (dummy student)",
                System.currentTimeMillis()
            )
            createSeedComment(dummyComment)

            //============================================================================================================================================================

            // FACULTIES

            val commFaculty = Faculty(
                "fac_communication",
                "Faculty of Communication"
            )
            facultyDepartmentRepository.createFaculty(commFaculty)

            //TODO ADD MORE FACULTIES
            //...
            //...

            //============================================================================================================================================================
            // DEPARTMENTS

            val advDesCommDepartment = Department(
                "dept_advertising_design_and_communication",
                "Advertising Design and Communication",
                "fac_communication"
            )
            facultyDepartmentRepository.createDepartment(advDesCommDepartment)

            val journalismDepartment = Department(
                "dept_journalism",
                "Journalism",
                "fac_communication"
            )
            facultyDepartmentRepository.createDepartment(journalismDepartment)

            val pubRelPubDepartment = Department(
                "dept_public_relations_and_publicity",
                "Public Relations and Publicity",
                "fac_communication"
            )
            facultyDepartmentRepository.createDepartment(pubRelPubDepartment)

            val RTFDepartment = Department(
                "dept_radio_television_and_film_studies",
                "Radio, Television and Film Studies",
                "fac_communication"
            )
            facultyDepartmentRepository.createDepartment(RTFDepartment)

            val VCDDepartment = Department(
                "dept_visual_communication_design",
                "Visual Communication Design",
                "fac_communication"
            )
            facultyDepartmentRepository.createDepartment(VCDDepartment)

            //TODO ADD MORE DEPARTMENTS
            //...
            //...

            //============================================================================================================================================================
            // COURSES

            val vcd111 = Course(
                "vcd111",
                "VCD111",
                "Basic Drawing",
                "fac_communication",
                6,
                3,
                "vcd111",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd111)

            val vcd171 = Course(
                "vcd171",
                "VCD171",
                "Design Fundamentals",
                "fac_communication",
                5,
                3,
                "vcd171",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd171)

            val vcd172 = Course(
                "vcd172",
                "VCD172",
                "Digital Design",
                "fac_communication",
                7,
                3,
                "vcd172",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd172)

            val vcd271 = Course(
                "vcd271",
                "VCD271",
                "Modeling in Virtual Environments",
                "fac_communication",
                6,
                3,
                "vcd271",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd271)

            val vcd273 = Course(
                "vcd273",
                "VCD273",
                "Digital Design and Illustration",
                "fac_communication",
                7,
                3,
                "vcd273",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd273)

            val vcd210 = Course(
                "vcd210",
                "VCD210",
                "Contemporary Art Concepts",
                "fac_communication",
                5,
                3,
                "vcd210",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd210)

            val vcd277 = Course(
                "vcd277",
                "VCD277",
                "2D Animation",
                "fac_communication",
                5,
                3,
                "vcd277",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd277)

            val vcd370 = Course(
                "vcd370",
                "VCD370",
                "Creative Brand Design",
                "fac_communication",
                5,
                3,
                "vcd370",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd370)

            val vcd382 = Course(
                "vcd382",
                "VCD382",
                "Sound Studio",
                "fac_communication",
                5,
                3,
                "vcd382",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd382)

            val vcd402 = Course(
                "vcd402",
                "VCD402",
                "Film and Animation Analysis",
                "fac_communication",
                5,
                3,
                "vcd402",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd402)

            val vcd423 = Course(
                "vcd423",
                "VCD423",
                "Art Sociology",
                "fac_communication",
                5,
                3,
                "vcd423",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd423)

            val vcd258 = Course(
                "vcd258",
                "VCD258",
                "Visual Storytelling Workshop",
                "fac_communication",
                5,
                3,
                "vcd258",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd258)

            val vcd272 = Course(
                "vcd272",
                "VCD272",
                "Motion Design in 3D",
                "fac_communication",
                7,
                3,
                "vcd272",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd272)

            val vcd274 = Course(
                "vcd274",
                "VCD274",
                "Motion Graphics",
                "fac_communication",
                6,
                3,
                "vcd274",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd274)

            val vcd311 = Course(
                "vcd311",
                "VCD311",
                "Introduction to Digital Video",
                "fac_communication",
                6,
                3,
                "vcd311",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd311)

            val vcd321 = Course(
                "vcd321",
                "VCD321",
                "Cultural Icons in Design",
                "fac_communication",
                3,
                3,
                "vcd321",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd321)

            val vcd371 = Course(
                "vcd371",
                "VCD371",
                "Introduction to Game Design",
                "fac_communication",
                5,
                3,
                "vcd371",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd371)

            val vcd373 = Course(
                "vcd373",
                "VCD373",
                "Spatial Desing Rendering",
                "fac_communication",
                5,
                3,
                "vcd373",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd373)

            val vcd312 = Course(
                "vcd312",
                "VCD312",
                "Digital Video Production",
                "fac_communication",
                4,
                3,
                "vcd312",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd312)

            val vcd372 = Course(
                "vcd372",
                "VCD372",
                "Game Design Development",
                "fac_communication",
                5,
                3,
                "vcd372",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd372)

            val vcd378 = Course(
                "vcd378",
                "VCD378",
                "Creative Thinking",
                "fac_communication",
                3,
                3,
                "vcd378",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd378)

            val vcd384 = Course(
                "vcd384",
                "VCD384",
                "Visual Communication Design Workshop",
                "fac_communication",
                4,
                3,
                "vcd384",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd384)

            val vcd421 = Course(
                "vcd421",
                "VCD421",
                "Semiotic Approaches to Design",
                "fac_communication",
                5,
                3,
                "vcd421",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd421)

            val vcd471 = Course(
                "vcd471",
                "VCD471",
                "Interactive Design Studio",
                "fac_communication",
                6,
                3,
                "vcd471",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd471)

            val vcd470 = Course(
                "vcd470",
                "VCD470",
                "Art Direction in Games",
                "fac_communication",
                10,
                3,
                "vcd470",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd470)

            val vcd400 = Course(
                "vcd400",
                "VCD400",
                "Interdisciplinary Communication Design Practices",
                "fac_communication",
                10,
                3,
                "vcd400",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd400)

            val vcd474 = Course(
                "vcd474",
                "VCD474",
                "Game Design Workshop",
                "fac_communication",
                10,
                3,
                "vcd474",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd474)

            val vcd422 = Course(
                "vcd422",
                "VCD422",
                "Digital Culture",
                "fac_communication",
                6,
                3,
                "vcd422",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd422)

            val vcd436 = Course(
                "vcd436",
                "VCD436",
                "Product Concept Development",
                "fac_communication",
                6,
                3,
                "vcd436",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd436)

            val vcd492 = Course(
                "vcd492",
                "VCD492",
                "Graduation Project",
                "fac_communication",
                8,
                3,
                "vcd492",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            courseRepository.createCourse(vcd492)

            //TODO ADD MORE COURSES
            //...
            //...

            //============================================================================================================================================================
            // INSTRUCTORS

            val vcd_instructor1 = Instructor(
                "vcd_instructor1_neda_ucer",
                "fac_communication",
                "Neda Üçer",
                "nsaracer@yeditepe.edu.tr",
                "1636",
                "Prof.",
                "neda üçer",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            instructorRepository.createInstructor(vcd_instructor1)

            val vcd_instructor2 = Instructor(
                "vcd_instructor2_tugrul_tanyol",
                "fac_communication",
                "Tuğrul Tanyol",
                "tugrul.tanyol@yeditepe.edu.tr",
                "1824",
                "Assoc. Prof. Dr.",
                "tuğrul tanyol",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            instructorRepository.createInstructor(vcd_instructor2)

            val vcd_instructor3 = Instructor(
                "vcd_instructor3_cem_boluktas",
                "fac_communication",
                "Cem Bölüktaş",
                "cem.boluktas@yeditepe.edu.tr",
                "3961",
                "Asst. Prof. Dr.",
                "cem bölüktaş",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            instructorRepository.createInstructor(vcd_instructor3)

            val vcd_instructor4 = Instructor(
                "vcd_instructor4_merve_caskurlu",
                "fac_communication",
                "Merve Çaşkurlu",
                "merve.caskurlu@yeditepe.edu.tr",
                "2759",
                "Asst. Prof. Dr.",
                "merve çaşkurlu",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            instructorRepository.createInstructor(vcd_instructor4)

            val vcd_instructor5 = Instructor(
                "vcd_instructor5_irem_tekin_yucesoy",
                "fac_communication",
                "İrem Tekin Yücesoy",
                "irem.tekin@yeditepe.edu.tr",
                "2763",
                "Asst. Prof. Dr.",
                "irem tekin yücesoy",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            instructorRepository.createInstructor(vcd_instructor5)

            val vcd_instructor6 = Instructor(
                "vcd_instructor6_gul_bakan",
                "fac_communication",
                "Gül Bakan",
                "gbakan@yeditepe.edu.tr",
                "3769",
                "Academic Staff",
                "gül bakan",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            instructorRepository.createInstructor(vcd_instructor6)

            val vcd_instructor7 = Instructor(
                "vcd_instructor7_gokhan_gurbuz",
                "fac_communication",
                "Gökhan Gürbüz",
                "gokhan.gurbuz@yeditepe.edu.tr",
                "1662",
                "Lecturer",
                "gökhan gürbüz",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            instructorRepository.createInstructor(vcd_instructor7)

            val vcd_instructor8 = Instructor(
                "vcd_instructor8_yasemin_ulgen_muluk",
                "fac_communication",
                "Yasemin Ülgen Muluk",
                "ymuluk@yeditepe.edu.tr",
                "2591",
                "Lecturer",
                "yasemin ülgen muluk",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            instructorRepository.createInstructor(vcd_instructor8)

            val vcd_instructor9 = Instructor(
                "vcd_instructor9_beyda_kirci",
                "fac_communication",
                "Beyda Kırcı",
                "beyda.kirci@yeditepe.edu.tr",
                "1779",
                "Research Assistant",
                "beyda kırcı",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            instructorRepository.createInstructor(vcd_instructor9)

            val vcd_instructor10 = Instructor(
                "vcd_instructor10_nehir_dagli",
                "fac_communication",
                "Nehir Dağlı",
                "nehir.dagli@yeditepe.edu.tr",
                "1779",
                "Research Assistant",
                "nehir dağlı",
                listOf(
                    "dept_visual_communication_design"
                ),
                emptyList()
            )
            instructorRepository.createInstructor(vcd_instructor10)

            Log.d(TAG, "SEEDING COMPLETED")

            //TODO ADD MORE INSTRUCTORS
            //...
            //...


        } catch (e: Exception) {
            Log.e(TAG, "SEEDING FAILED", e)
        }
    }
}

/*
//MAIN ACTIVITYE BUNU EKLE

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    // Seeder'ı buraya enjekte et
    @Inject lateinit var seederRepository: SeederRepository

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // --- GEÇİCİ KOD BAŞLANGICI ---
        // Uygulama açılır açılmaz veritabanını doldurur.
        // Logcat'i izle, "SEEDING COMPLETED" yazısını görünce
        // BU KODU SİL VEYA YORUMA AL.
        /*
        lifecycleScope.launch {
            seederRepository.seedDatabase()
        }
        */
        // --- GEÇİCİ KOD SONU ---

        setContent {
            // ... Senin AppNavigation kodun ...
        }
    }
}
*/