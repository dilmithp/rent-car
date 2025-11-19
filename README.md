# Smart Car Rental System 🚗

A complete car rental management system built with Spring Boot, featuring admin and customer portals with LKR currency support.

## 🚀 Quick Start

```bash
# Set Java 23
export JAVA_HOME=$(/usr/libexec/java_home -v 23)

# Run the application
mvn spring-boot:run
```

**Access**: http://localhost:8080

## 👥 Default Accounts

- **Admin**: `admin` / `admin123`
- **Customer**: Register at `/register`

## 📚 Documentation

All documentation, scripts, and setup guides are in the **`docs/`** folder.

**Start here**: [`docs/START_HERE.md`](docs/START_HERE.md)

### Key Documents
- [Quick Start Guide](docs/QUICK_START.md)
- [How to Run](docs/HOW_TO_RUN.md)
- [Database Setup](docs/DATABASE_IMPORT_GUIDE.md)
- [Complete Project Summary](docs/PROJECT_COMPLETE_SUMMARY.md)
- [Documentation Index](docs/INDEX.md)

## ✨ Features

### Admin Portal
- Dashboard with statistics
- Vehicle management (CRUD)
- Customer management (CRUD)
- Booking management
- Payment verification
- PDF exports

### Customer Portal
- Browse available vehicles
- Book vehicles with date selection
- View booking history
- Upload payment slips
- Download receipts
- Profile management

## 💰 Currency

All amounts displayed in **LKR (Sri Lankan Rupee)**

## 🛠 Tech Stack

- **Backend**: Spring Boot 3.2.0, Spring Security, Spring Data JPA
- **Frontend**: Thymeleaf, Bootstrap 5
- **Database**: MySQL (MAMP)
- **Java**: 23 (compatible with 21)
- **PDF**: OpenPDF

## 📊 Database

- **Name**: `car_rental_system`
- **Setup**: Import from `docs/complete_database_with_data.sql`

## 🎯 Status

✅ **100% Complete & Working**
- All CRUD operations functional
- All lazy loading issues resolved
- Currency changed to LKR
- Customer & admin portals working

## 📞 Need Help?

Check the [`docs/`](docs/) folder for comprehensive documentation and guides.

---

**Version**: 1.0  
**Last Updated**: November 19, 2025  
**License**: MIT

