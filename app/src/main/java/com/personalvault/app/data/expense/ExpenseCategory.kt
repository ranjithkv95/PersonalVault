package com.personalvault.app.data.expense

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.AccountBalanceWallet
import androidx.compose.material.icons.filled.Bolt
import androidx.compose.material.icons.filled.Cake
import androidx.compose.material.icons.filled.CardGiftcard
import androidx.compose.material.icons.filled.CardTravel
import androidx.compose.material.icons.filled.Celebration
import androidx.compose.material.icons.filled.ChildCare
import androidx.compose.material.icons.filled.CreditCard
import androidx.compose.material.icons.filled.DirectionsCar
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.LocalCafe
import androidx.compose.material.icons.filled.MedicalServices
import androidx.compose.material.icons.filled.Movie
import androidx.compose.material.icons.filled.MoreHoriz
import androidx.compose.material.icons.filled.Restaurant
import androidx.compose.material.icons.filled.School
import androidx.compose.material.icons.filled.Shield
import androidx.compose.material.icons.filled.ShoppingBag
import androidx.compose.material.icons.filled.Spa
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.vector.ImageVector

data class ExpenseSubcategory(
    val id: String,
    val name: String
)

/**
 * Curated Indian personal-expense taxonomy. Each top-level category has a list of
 * subcategories that reflect common spend patterns in urban India (UPI, Swiggy,
 * auto/Ola, EMIs, society maintenance, festivals, etc.).
 */
data class ExpenseCategory(
    val id: String,
    val name: String,
    val icon: ImageVector,
    val color: Color,
    val subcategories: List<ExpenseSubcategory>
) {
    companion object {
        val FOOD = ExpenseCategory(
            "food", "Food & Dining",
            Icons.Default.Restaurant, Color(0xFFFB8C00),
            listOf(
                ExpenseSubcategory("groceries", "Groceries"),
                ExpenseSubcategory("swiggy_zomato", "Swiggy / Zomato"),
                ExpenseSubcategory("restaurant", "Restaurants"),
                ExpenseSubcategory("street_food", "Street Food / Chaat"),
                ExpenseSubcategory("tea_coffee", "Tea / Coffee / Juice"),
                ExpenseSubcategory("office_lunch", "Office Lunch / Tiffin"),
                ExpenseSubcategory("bakery", "Bakery / Sweets"),
                ExpenseSubcategory("alcohol", "Alcohol")
            )
        )

        val TRANSPORT = ExpenseCategory(
            "transport", "Transportation",
            Icons.Default.DirectionsCar, Color(0xFF1E88E5),
            listOf(
                ExpenseSubcategory("fuel", "Petrol / Diesel"),
                ExpenseSubcategory("auto", "Auto Rickshaw"),
                ExpenseSubcategory("cab", "Uber / Ola / Rapido"),
                ExpenseSubcategory("metro_bus", "Metro / Bus"),
                ExpenseSubcategory("train", "Train / IRCTC"),
                ExpenseSubcategory("parking", "Parking / Toll / FASTag"),
                ExpenseSubcategory("vehicle_service", "Vehicle Service / Repair"),
                ExpenseSubcategory("vehicle_insurance", "Vehicle Insurance / PUC")
            )
        )

        val HOUSING = ExpenseCategory(
            "housing", "Housing",
            Icons.Default.Home, Color(0xFF6D4C41),
            listOf(
                ExpenseSubcategory("rent", "Rent"),
                ExpenseSubcategory("home_loan_emi", "Home Loan EMI"),
                ExpenseSubcategory("society", "Society Maintenance"),
                ExpenseSubcategory("repairs", "Repairs & Renovation"),
                ExpenseSubcategory("property_tax", "Property Tax"),
                ExpenseSubcategory("domestic_help", "Domestic Help / Maid"),
                ExpenseSubcategory("cook", "Cook / Milkman / Paper")
            )
        )

        val UTILITIES = ExpenseCategory(
            "utilities", "Utilities & Bills",
            Icons.Default.Bolt, Color(0xFFFDD835),
            listOf(
                ExpenseSubcategory("electricity", "Electricity"),
                ExpenseSubcategory("water", "Water"),
                ExpenseSubcategory("gas", "Gas (LPG / PNG)"),
                ExpenseSubcategory("internet", "Broadband / WiFi"),
                ExpenseSubcategory("mobile", "Mobile Recharge / Postpaid"),
                ExpenseSubcategory("dth", "DTH / Cable TV")
            )
        )

        val SHOPPING = ExpenseCategory(
            "shopping", "Shopping",
            Icons.Default.ShoppingBag, Color(0xFFE53935),
            listOf(
                ExpenseSubcategory("clothing", "Clothing & Apparel"),
                ExpenseSubcategory("electronics", "Electronics / Gadgets"),
                ExpenseSubcategory("amazon_flipkart", "Amazon / Flipkart / Myntra"),
                ExpenseSubcategory("household", "Household / Kitchen"),
                ExpenseSubcategory("jewellery", "Jewellery / Gold"),
                ExpenseSubcategory("footwear", "Footwear / Accessories")
            )
        )

        val HEALTH = ExpenseCategory(
            "health", "Health & Medical",
            Icons.Default.MedicalServices, Color(0xFF43A047),
            listOf(
                ExpenseSubcategory("doctor", "Doctor / Consultation"),
                ExpenseSubcategory("pharmacy", "Pharmacy / Medicines"),
                ExpenseSubcategory("tests", "Lab Tests / Scans"),
                ExpenseSubcategory("hospital", "Hospital / Surgery"),
                ExpenseSubcategory("dental", "Dental"),
                ExpenseSubcategory("gym", "Gym / Yoga / Fitness"),
                ExpenseSubcategory("supplements", "Supplements / Protein")
            )
        )

        val ENTERTAINMENT = ExpenseCategory(
            "entertainment", "Entertainment",
            Icons.Default.Movie, Color(0xFF8E24AA),
            listOf(
                ExpenseSubcategory("movies", "Movies / PVR / INOX"),
                ExpenseSubcategory("ott", "OTT (Netflix / Prime / Hotstar)"),
                ExpenseSubcategory("music", "Spotify / YouTube Music"),
                ExpenseSubcategory("events", "Events / Concerts"),
                ExpenseSubcategory("games", "Games / Apps / IAPs"),
                ExpenseSubcategory("books", "Books / Kindle")
            )
        )

        val EDUCATION = ExpenseCategory(
            "education", "Education",
            Icons.Default.School, Color(0xFF0097A7),
            listOf(
                ExpenseSubcategory("school_fees", "School / College Fees"),
                ExpenseSubcategory("tuition", "Tuition / Coaching"),
                ExpenseSubcategory("books", "Books / Stationery"),
                ExpenseSubcategory("online_courses", "Online Courses (Udemy / Coursera)"),
                ExpenseSubcategory("exams", "Exam Fees")
            )
        )

        val PERSONAL_CARE = ExpenseCategory(
            "personal_care", "Personal Care",
            Icons.Default.Spa, Color(0xFFEC407A),
            listOf(
                ExpenseSubcategory("salon", "Salon / Barber"),
                ExpenseSubcategory("spa", "Spa / Massage"),
                ExpenseSubcategory("cosmetics", "Cosmetics / Skincare"),
                ExpenseSubcategory("laundry", "Laundry / Dry Clean")
            )
        )

        val TRAVEL = ExpenseCategory(
            "travel", "Travel & Vacation",
            Icons.Default.CardTravel, Color(0xFF00ACC1),
            listOf(
                ExpenseSubcategory("flights", "Flights"),
                ExpenseSubcategory("hotels", "Hotels / Airbnb"),
                ExpenseSubcategory("vacation", "Vacation Package"),
                ExpenseSubcategory("visa", "Visa / Passport"),
                ExpenseSubcategory("forex", "Forex / Travel Insurance")
            )
        )

        val FINANCIAL = ExpenseCategory(
            "financial", "Financial",
            Icons.Default.CreditCard, Color(0xFF3949AB),
            listOf(
                ExpenseSubcategory("cc_bill", "Credit Card Bill"),
                ExpenseSubcategory("personal_loan_emi", "Personal Loan EMI"),
                ExpenseSubcategory("taxes", "Taxes / GST / TDS"),
                ExpenseSubcategory("bank_charges", "Bank Charges / Fees"),
                ExpenseSubcategory("interest", "Interest / Penalties")
            )
        )

        val FAMILY = ExpenseCategory(
            "family", "Family & Kids",
            Icons.Default.ChildCare, Color(0xFF7CB342),
            listOf(
                ExpenseSubcategory("kids_activities", "Kids Activities"),
                ExpenseSubcategory("toys", "Toys / Games"),
                ExpenseSubcategory("pocket_money", "Pocket Money / Allowance"),
                ExpenseSubcategory("elderly_care", "Parents / Elderly Care")
            )
        )

        val GIFTS = ExpenseCategory(
            "gifts", "Gifts & Donations",
            Icons.Default.CardGiftcard, Color(0xFFD81B60),
            listOf(
                ExpenseSubcategory("gifts", "Gifts"),
                ExpenseSubcategory("charity", "Charity / NGO"),
                ExpenseSubcategory("dakshina", "Dakshina / Prasad"),
                ExpenseSubcategory("tips", "Tips / Bakshish")
            )
        )

        val FESTIVALS = ExpenseCategory(
            "festivals", "Festivals & Events",
            Icons.Default.Celebration, Color(0xFFF4511E),
            listOf(
                ExpenseSubcategory("diwali", "Diwali"),
                ExpenseSubcategory("wedding", "Wedding"),
                ExpenseSubcategory("puja", "Puja / Rituals"),
                ExpenseSubcategory("birthday", "Birthday"),
                ExpenseSubcategory("anniversary", "Anniversary"),
                ExpenseSubcategory("other_festival", "Other Festival")
            )
        )

        val INSURANCE = ExpenseCategory(
            "insurance", "Insurance Premiums",
            Icons.Default.Shield, Color(0xFF546E7A),
            listOf(
                ExpenseSubcategory("life", "Life Insurance"),
                ExpenseSubcategory("health", "Health Insurance"),
                ExpenseSubcategory("vehicle", "Vehicle Insurance"),
                ExpenseSubcategory("home", "Home Insurance")
            )
        )

        val SUBSCRIPTIONS = ExpenseCategory(
            "subscriptions", "Subscriptions",
            Icons.Default.LocalCafe, Color(0xFF5E35B1),
            listOf(
                ExpenseSubcategory("sw_subs", "Software / Apps"),
                ExpenseSubcategory("news", "News / Magazines"),
                ExpenseSubcategory("cloud", "Cloud Storage"),
                ExpenseSubcategory("memberships", "Memberships (Costco / Decathlon)")
            )
        )

        val PETS = ExpenseCategory(
            "pets", "Pets",
            Icons.Default.Favorite, Color(0xFF8D6E63),
            listOf(
                ExpenseSubcategory("pet_food", "Pet Food"),
                ExpenseSubcategory("vet", "Vet / Grooming"),
                ExpenseSubcategory("pet_acc", "Accessories")
            )
        )

        val MISC = ExpenseCategory(
            "misc", "Miscellaneous",
            Icons.Default.MoreHoriz, Color(0xFF616161),
            listOf(
                ExpenseSubcategory("cash_withdrawal", "Cash Withdrawal"),
                ExpenseSubcategory("other", "Other")
            )
        )

        val BIRTHDAYS = ExpenseCategory(
            "lifestyle", "Lifestyle",
            Icons.Default.Cake, Color(0xFFFFB300),
            listOf(
                ExpenseSubcategory("hobbies", "Hobbies"),
                ExpenseSubcategory("clubs", "Clubs / Society"),
                ExpenseSubcategory("dating", "Dating / Outings")
            )
        )

        val WALLET = ExpenseCategory(
            "reload", "Wallet Reloads",
            Icons.Default.AccountBalanceWallet, Color(0xFF00897B),
            listOf(
                ExpenseSubcategory("fastag_reload", "FASTag Reload"),
                ExpenseSubcategory("wallet_reload", "Paytm / Mobikwik Reload"),
                ExpenseSubcategory("metro_card", "Metro Card Reload")
            )
        )

        val ALL: List<ExpenseCategory> = listOf(
            FOOD, TRANSPORT, HOUSING, UTILITIES, SHOPPING, HEALTH,
            ENTERTAINMENT, EDUCATION, PERSONAL_CARE, TRAVEL, FINANCIAL,
            FAMILY, GIFTS, FESTIVALS, INSURANCE, SUBSCRIPTIONS, PETS,
            BIRTHDAYS, WALLET, MISC
        )

        fun fromId(id: String?): ExpenseCategory =
            ALL.firstOrNull { it.id == id } ?: MISC

        fun subcategoryName(categoryId: String?, subId: String?): String? {
            if (categoryId == null || subId == null) return null
            return fromId(categoryId).subcategories.firstOrNull { it.id == subId }?.name
        }
    }
}
