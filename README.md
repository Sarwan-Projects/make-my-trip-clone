# MakeMyTour - Full Stack Travel Booking Platform

A comprehensive travel booking platform with 6 advanced features including cancellation & refunds, review system, live flight status, seat/room selection, dynamic pricing, and AI recommendations.

## 🌐 Live Demo
- **Frontend**: https://make-my-trip-clone-1-s4of.onrender.com
- **Backend API**: https://make-my-trip-clone-pb3x.onrender.com
- **Dashboard**: https://make-my-trip-clone-1-s4of.onrender.com/dashboard

## 🚀 Features

### 1. Cancellation & Refunds
- **Smart Refund Calculation**: Automatic refund calculation based on cancellation time and reason
- **Flexible Cancellation Reasons**: Medical, emergency, change of plans, etc.
- **Refund Status Tracking**: Real-time tracking of refund processing
- **Policy-based Refunds**: 90% refund (48h+), 50% refund (24h+), 25% refund (2h+)

### 2. Review & Rating System
- **5-Star Rating System**: Rate flights and hotels with detailed reviews
- **Photo Upload**: Add photos to reviews for better context
- **Helpful Voting**: Community-driven review ranking
- **Verified Reviews**: Only verified bookings can leave reviews
- **Business Replies**: Hotels/airlines can respond to reviews
- **Content Moderation**: Flag inappropriate content

### 3. Live Flight Status (Mock API)
- **Real-time Updates**: Live flight status with delay information
- **Delay Reasons**: Detailed explanations for delays
- **Gate & Terminal Info**: Complete flight information
- **Push Notifications**: Instant updates for flight changes
- **Multiple Flight Tracking**: Track all your flights in one place

### 4. Seat/Room Selection
- **Interactive Seat Maps**: Visual seat selection for flights
- **Premium Seat Upselling**: Business, premium economy options
- **Room Type Selection**: Standard, deluxe, suite options
- **3D Room Previews**: Virtual room tours
- **Preference Saving**: Remember user preferences

### 5. Dynamic Pricing Engine
- **Demand-based Pricing**: Prices adjust based on demand
- **Holiday Pricing**: Special pricing for holidays and weekends
- **Price History**: Track price changes over time
- **Price Freeze**: Lock prices for 24 hours
- **Smart Recommendations**: Best time to book alerts

### 6. AI Recommendations
- **Personalized Suggestions**: Based on booking history and preferences
- **Collaborative Filtering**: Learn from similar users
- **Feedback Loop**: Improve recommendations based on user feedback
- **Explanation System**: "Why this recommendation?" tooltips
- **Multi-factor Analysis**: Price, location, amenities, reviews

## 🛠 Technology Stack

### Backend
- **Java 17** with **Spring Boot 3.5.5**
- **MongoDB Atlas** for data persistence
- **Spring Security** for authentication
- **Spring WebSocket** for real-time notifications
- **Maven** for dependency management
- **Deployed on Render**

### Frontend
- **Next.js 15** with **React 19**
- **TypeScript** for type safety
- **Tailwind CSS** for styling
- **Custom UI Components** (no external UI library dependencies)
- **Redux Toolkit** for state management
- **Custom SVG Charts** for data visualization
- **Axios** for API communication
- **Deployed on Render**

## 📦 Installation & Setup

### Prerequisites
- Java 17+
- Node.js 18+
- MongoDB 4.4+
- Maven 3.6+

### Backend Setup

1. **Clone the repository**
   ```bash
   git clone <repository-url>
   cd makemytrip
   ```

2. **Configure MongoDB**
   ```bash
   # Start MongoDB service
   mongod --dbpath /path/to/your/db
   ```

3. **Update application.properties**
   ```properties
   spring.data.mongodb.uri=mongodb://localhost:27017/makemytrip
   spring.data.mongodb.database=makemytrip
   server.port=8080
   ```

4. **Build and run the backend**
   ```bash
   mvn clean install
   mvn spring-boot:run
   ```

### Frontend Setup

1. **Navigate to frontend directory**
   ```bash
   cd makemytour
   ```

2. **Install dependencies**
   ```bash
   npm install
   ```

3. **Start development server**
   ```bash
   npm run dev
   ```

4. **Access the application**
   - Frontend: http://localhost:3000
   - Backend API: http://localhost:8080

## 🎯 API Endpoints

### Cancellation & Refunds
- `GET /api/cancellation/bookings/{userId}` - Get user bookings
- `POST /api/cancellation/calculate-refund` - Calculate refund amount
- `POST /api/cancellation/cancel` - Cancel booking
- `PUT /api/cancellation/refund-status` - Update refund status

### Reviews
- `POST /api/reviews` - Create review
- `GET /api/reviews/{itemId}/{itemType}` - Get reviews
- `POST /api/reviews/{reviewId}/helpful` - Mark review helpful
- `POST /api/reviews/{reviewId}/flag` - Flag review
- `GET /api/reviews/{itemId}/{itemType}/average-rating` - Get average rating

### Flight Status
- `GET /api/flight-status/{flightId}` - Get flight status
- `GET /api/flight-status/by-number/{flightNumber}` - Get status by flight number
- `POST /api/flight-status/user-flights` - Get multiple flight statuses

### Seat Selection
- `GET /api/seat-selection/flight/{flightId}` - Get seat map
- `POST /api/seat-selection/book-seats` - Book seats
- `POST /api/seat-selection/calculate-upgrade-price` - Calculate upgrade cost

### Room Selection
- `GET /api/room-selection/hotel/{hotelId}` - Get room layout
- `POST /api/room-selection/book-room` - Book room
- `GET /api/room-selection/hotel/{hotelId}/available/{roomType}` - Get available rooms

### Pricing
- `POST /api/pricing/calculate` - Calculate dynamic price
- `GET /api/pricing/history/{itemId}/{itemType}` - Get price history
- `GET /api/pricing/insights/{itemId}/{itemType}` - Get price insights
- `POST /api/pricing/freeze` - Freeze price

### Recommendations
- `GET /api/recommendations/{userId}` - Get AI recommendations
- `POST /api/recommendations/{recommendationId}/feedback` - Provide feedback

## 🎨 UI Components

### Dashboard
- **Overview Tab**: Booking statistics and quick actions
- **Bookings Tab**: Manage all bookings with cancellation options
- **Flight Status Tab**: Real-time flight tracking
- **Reviews Tab**: Write and manage reviews
- **Seat/Room Tab**: Interactive selection interfaces
- **Pricing Tab**: Price tracking and freeze options
- **AI Recommendations Tab**: Personalized suggestions

### Key Features
- **Responsive Design**: Works on all devices
- **Real-time Updates**: WebSocket integration
- **Interactive Components**: Seat maps, room layouts
- **Data Visualization**: Price history charts
- **Modern UI**: Clean, intuitive interface

## 🔧 Configuration

### MongoDB Collections
- `users` - User accounts with embedded bookings
- `reviews` - User reviews and ratings
- `flight_status` - Real-time flight information
- `seat_maps` - Flight seat configurations
- `room_layouts` - Hotel room layouts
- `price_history` - Price tracking data
- `recommendations` - AI-generated suggestions
- `user_preferences` - User behavior and preferences

**Note:** Bookings are stored as embedded documents within the `users` collection to maintain data consistency and simplify queries.

### Environment Variables
```bash
# Backend
MONGODB_URI=mongodb://localhost:27017/makemytrip
SERVER_PORT=8080

# Frontend
NEXT_PUBLIC_API_URL=http://localhost:8080
NEXT_PUBLIC_WS_URL=http://localhost:8080/ws
```

## 🚀 Deployment

### Backend Deployment
```bash
# Build JAR file
mvn clean package

# Run with production profile
java -jar target/makemytrip-0.0.1-SNAPSHOT.jar --spring.profiles.active=prod
```

### Frontend Deployment
```bash
# Build for production
npm run build

# Start production server
npm start
```

## 🧪 Testing

### Complete Project Test
```bash
./test-all.sh
```

### Individual Tests
```bash
# Backend only
./test-backend.sh

# Frontend only  
./test-frontend.sh

# API endpoints
./test-api.sh
```

## 📱 Mobile Responsiveness

The application is fully responsive and works seamlessly on:
- Desktop (1200px+)
- Tablet (768px - 1199px)
- Mobile (320px - 767px)

## ✨ Project Status: Production Ready

### 🎯 All Features Implemented & Working
- ✅ **Original Features**: User auth, flight/hotel booking, admin panel
- ✅ **6 New Advanced Features**: All implemented and deployed
- ✅ **Frontend**: Fully responsive, TypeScript error-free
- ✅ **Backend**: All APIs working, proper error handling
- ✅ **Database**: MongoDB Atlas integration working
- ✅ **Deployment**: Both frontend and backend live on Render

### 🔧 Technical Achievements
- ✅ **Zero Dependencies Issues**: Removed problematic external libraries
- ✅ **Custom UI Components**: Built from scratch for reliability
- ✅ **Type Safety**: Full TypeScript implementation
- ✅ **API Integration**: All endpoints properly connected
- ✅ **Responsive Design**: Works on all device sizes
- ✅ **Production Deployment**: Live and accessible

## 🚀 Deployment

### Local Development
```bash
# Backend
mvn spring-boot:run

# Frontend (in separate terminal)
cd makemytour
npm install
npm run dev
```

### Render Deployment
1. **Backend**: Deploy as Web Service using `Dockerfile`
2. **Frontend**: Deploy as Static Site from `makemytour` folder
3. **Database**: Use Render's MongoDB or your existing MongoDB Atlas

### 🚀 Access the Live Application
- **Main App**: https://make-my-trip-clone-1-s4of.onrender.com
- **Dashboard**: https://make-my-trip-clone-1-s4of.onrender.com/dashboard
- **API Endpoints**: https://make-my-trip-clone-pb3x.onrender.com/api/*

### 🔧 Local Development Setup
```bash
# Backend
mvn spring-boot:run

# Frontend (separate terminal)
cd makemytour
npm install
npm run dev
```

### 🚀 Production Build
```bash
# Frontend build and start
cd makemytour
npm run build
npm start
```

### 📊 Database
- **MongoDB Atlas**: Fully configured and connected
- **Collections**: Users (with embedded bookings), Reviews, Flight Status, etc.
- **Sample Data**: Auto-initialized on first run

## 🎯 Complete Feature Set

### Original Features (Working)
- ✅ **User Authentication**: Login/signup with secure sessions
- ✅ **Flight Booking**: Search, select, and book flights
- ✅ **Hotel Booking**: Browse and book hotel rooms
- ✅ **Admin Panel**: Manage flights, hotels, and bookings
- ✅ **User Profile**: Edit profile and view booking history

### 6 New Advanced Features (Implemented & Live)
1. ✅ **Cancellation & Refunds**: Smart refund calculation based on timing and reason
2. ✅ **Review & Rating System**: 5-star ratings, photo uploads, helpful voting
3. ✅ **Live Flight Status**: Real-time status with mock API, delay notifications
4. ✅ **Seat/Room Selection**: Interactive maps, premium upselling, 3D previews
5. ✅ **Dynamic Pricing**: Demand-based pricing, history charts, price freeze
6. ✅ **AI Recommendations**: Personalized suggestions with feedback learning

## 🏗️ Architecture

```
Frontend (Next.js) → API Layer → Backend (Spring Boot) → MongoDB Atlas
     ↓                   ↓              ↓                    ↓
Static Site on      REST APIs      Web Service on      Cloud Database
   Render                           Render
```

## 📱 Responsive Design
- **Desktop**: Full dashboard with all features
- **Tablet**: Optimized layout with touch interactions
- **Mobile**: Mobile-first responsive design

---

**🎉 Production-Ready Travel Booking Platform with 6 Advanced Features!**