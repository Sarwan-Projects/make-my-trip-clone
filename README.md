# MakeMyTour - Full Stack Travel Booking Platform

A comprehensive travel booking platform with 6 advanced features: cancellation & refunds, review system, live flight status, seat/room selection, dynamic pricing, and AI recommendations.

## 🌐 Live Demo
- **Frontend**: https://make-my-trip-clone-1-s4of.onrender.com
- **Backend API**: https://make-my-trip-clone-pb3x.onrender.com

## 🔐 Test Credentials

### User Account (Regular User)
- **Email**: `james.williams@gmail.com`
- **Password**: `456789`
- **Access**: Book flights/hotels, cancel bookings, write reviews, track prices

### Admin Account (Administrator)
- **Email**: `suraj@gmail.com`
- **Password**: `123456`
- **Access**: Admin panel, manage flights/hotels, view all users and bookings

## 🚀 6 Advanced Features

1. **Cancellation & Refunds** - Smart refund calculation (90% for 48h+, 50% for 24h+, 25% for 2h+) with real-time status tracking
2. **Review & Rating System** - 5-star ratings, photo uploads, helpful voting, verified reviews, content moderation
3. **Live Flight Status** - Real-time flight tracking with delay information, gate/terminal details
4. **Seat/Room Selection** - Interactive seat maps, premium upselling, room type selection (Standard/Deluxe/Suite)
5. **Dynamic Pricing Engine** - Demand-based pricing, price history charts, 24-hour price freeze
6. **AI Recommendations** - Personalized suggestions using collaborative filtering with feedback learning

## 🛠 Technology Stack

**Backend**: Java 17, Spring Boot 3.5.5, MongoDB Atlas, Spring Security, Maven  
**Frontend**: Next.js 15, React 19, TypeScript, Tailwind CSS, Redux Toolkit  
**Deployment**: Render (Frontend + Backend)

## 📦 Quick Start

**Prerequisites**: Java 17+, Node.js 18+, MongoDB 4.4+, Maven 3.6+

```bash
# Backend
mvn clean install
mvn spring-boot:run

# Frontend (new terminal)
cd makemytour
npm install
npm run dev
```

**Access**: http://localhost:3000 (Frontend), http://localhost:8080 (Backend)

## 🎯 Key API Endpoints

**Base URL**: https://make-my-trip-clone-pb3x.onrender.com

- **Booking**: `POST /booking/flight`, `POST /booking/hotel`, `GET /booking/user/{userId}`
- **Cancellation**: `POST /cancellation/calculate-refund`, `POST /cancellation/cancel`
- **Reviews**: `POST /api/reviews`, `GET /api/reviews/{itemId}/{itemType}`
- **Flight Status**: `GET /api/flight-status/{flightId}`
- **Seat/Room**: `GET /api/seat-selection/flight/{flightId}`, `GET /api/room-selection/hotel/{hotelId}`
- **Pricing**: `POST /api/pricing/calculate`, `POST /api/pricing/freeze`
- **AI Recommendations**: `GET /api/recommendations/{userId}`

## 🧪 Quick Test

### As User (james.williams@gmail.com / 456789):
1. Login → Book flight/hotel → View in Dashboard
2. Test: Cancel booking, Write review, Check flight status, Select seats/rooms, Track prices, View AI recommendations

### As Admin (suraj@gmail.com / 123456):
1. Login → Click "ADMIN" button
2. Manage flights, hotels, users, bookings

## 🔧 Configuration

**MongoDB Collections**: users, bookings, reviews, flight_status, seat_maps, room_layouts, price_history, recommendations

**Environment**:
```bash
MONGODB_URI=mongodb://localhost:27017/makemytrip
NEXT_PUBLIC_API_URL=http://localhost:8080
```

## 📱 Features
- ✅ Fully responsive (Desktop/Tablet/Mobile)
- ✅ Real-time updates
- ✅ Role-based access (User/Admin)
- ✅ Dynamic data from MongoDB
- ✅ Production-ready

---

**🎉 Full Stack Travel Booking Platform - Production Ready!**