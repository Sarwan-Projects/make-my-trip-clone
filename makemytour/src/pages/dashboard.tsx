import React, { useState, useEffect } from 'react';
import { useSelector } from 'react-redux';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import CancellationDialog from '../components/CancellationRefund/CancellationDialog';
import ReviewSystem from '../components/Reviews/ReviewSystem';
import FlightStatusTracker from '../components/FlightStatus/FlightStatusTracker';
import SeatMap from '../components/SeatSelection/SeatMap';
import RoomSelector from '../components/RoomSelection/RoomSelector';
import PriceTracker from '../components/Pricing/PriceTracker';
import AIRecommendations from '../components/Recommendations/AIRecommendations';
import { Button } from '../components/ui/button';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';
import {
  Calendar,
  CreditCard,
  Star,
  Plane,
  Hotel,
  TrendingUp,
  Sparkles,
  User,
  Settings
} from 'lucide-react';
import axios from 'axios';
import { API_BASE_URL } from '../config/api';

interface Booking {
  bookingId: string;
  type: string;
  itemId?: string;
  date: string;
  travelDate?: string;
  quantity: number;
  originalPrice?: number;
  totalPrice: number;
  status?: string;
  cancellationReason?: string;
  refundAmount?: number;
  refundStatus?: string;
}

const Dashboard: React.FC = () => {
  const [activeTab, setActiveTab] = useState('overview');
  const [userBookings, setUserBookings] = useState<Booking[]>([]);
  const [loading, setLoading] = useState(false);
  const user = useSelector((state: any) => state.user.user);
  const currentUserId = user?._id || '';

  useEffect(() => {
    fetchUserBookings();
  }, []);

  const fetchUserBookings = async () => {
    setLoading(true);
    try {
      // Fetch real bookings from API - no sample data
      const response = await axios.get(`${API_BASE_URL}/booking/user/${currentUserId}`);
      setUserBookings(response.data || []);
    } catch (error) {
      console.error('Error fetching bookings:', error);
      setUserBookings([]);
    } finally {
      setLoading(false);
    }
  };

  const handleCancellation = (bookingId: string) => {
    setUserBookings(prev =>
      prev.map(booking =>
        booking.bookingId === bookingId
          ? { ...booking, status: 'cancelled' }
          : booking
      )
    );
  };

  const getStatusColor = (status: string) => {
    switch (status) {
      case 'confirmed':
        return 'bg-green-100 text-green-800';
      case 'cancelled':
        return 'bg-red-100 text-red-800';
      case 'pending':
        return 'bg-yellow-100 text-yellow-800';
      default:
        return 'bg-gray-100 text-gray-800';
    }
  };

  const BookingCard: React.FC<{ booking: Booking }> = ({ booking }) => (
    <Card className="mb-4">
      <CardHeader className="pb-3">
        <div className="flex items-center justify-between">
          <CardTitle className="text-lg flex items-center gap-2">
            {booking.type === 'flight' ? <Plane className="h-5 w-5" /> : <Hotel className="h-5 w-5" />}
            {booking.type.charAt(0).toUpperCase() + booking.type.slice(1)} Booking
          </CardTitle>
          <span className={`px-2 py-1 rounded text-sm font-medium ${getStatusColor(booking.status || 'confirmed')}`}>
            {booking.status || 'confirmed'}
          </span>
        </div>
      </CardHeader>
      <CardContent>
        <div className="grid grid-cols-2 md:grid-cols-4 gap-4 mb-4">
          <div>
            <p className="text-sm text-gray-600">Booking Date</p>
            <p className="font-medium">{new Date(booking.date).toLocaleDateString()}</p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Travel Date</p>
            <p className="font-medium">{booking.travelDate ? new Date(booking.travelDate).toLocaleDateString() : 'N/A'}</p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Quantity</p>
            <p className="font-medium">{booking.quantity}</p>
          </div>
          <div>
            <p className="text-sm text-gray-600">Total Price</p>
            <p className="font-medium">${booking.totalPrice.toFixed(2)}</p>
          </div>
        </div>

        <div className="flex gap-2 flex-wrap">
          {booking.status === 'confirmed' && (
            <CancellationDialog booking={booking} onCancel={handleCancellation} userId={currentUserId} />
          )}

          <Button variant="outline" size="sm" onClick={() => setActiveTab('pricing')}>
            Track Price
          </Button>

          {booking.type === 'flight' && (
            <Button variant="outline" size="sm" onClick={() => setActiveTab('flight-status')}>
              Flight Status
            </Button>
          )}

          {booking.status === 'confirmed' && (
            <Button variant="outline" size="sm" onClick={() => setActiveTab('reviews')}>
              Write Review
            </Button>
          )}
        </div>

        {booking.status === 'cancelled' && booking.refundAmount && (
          <div className="mt-3 p-3 bg-blue-50 rounded-lg">
            <p className="text-sm">
              <strong>Refund:</strong> ${booking.refundAmount.toFixed(2)} -
              <span className={`ml-1 ${booking.refundStatus === 'processed' ? 'text-green-600' : 'text-yellow-600'}`}>
                {booking.refundStatus}
              </span>
            </p>
          </div>
        )}
      </CardContent>
    </Card>
  );

  return (
    <div className="min-h-screen bg-gray-50">
      <div className="container mx-auto px-4 py-8">
        <div className="mb-8">
          <h1 className="text-3xl font-bold text-gray-900 mb-2">Travel Dashboard</h1>
          <p className="text-gray-600">Manage your bookings, track prices, and discover new destinations</p>
        </div>

        <Tabs value={activeTab} onValueChange={setActiveTab} className="space-y-6">
          <TabsList className="grid w-full grid-cols-7">
            <TabsTrigger value="overview" className="flex items-center gap-2">
              <User className="h-4 w-4" />
              Overview
            </TabsTrigger>
            <TabsTrigger value="bookings" className="flex items-center gap-2">
              <Calendar className="h-4 w-4" />
              Bookings
            </TabsTrigger>
            <TabsTrigger value="flight-status" className="flex items-center gap-2">
              <Plane className="h-4 w-4" />
              Flight Status
            </TabsTrigger>
            <TabsTrigger value="reviews" className="flex items-center gap-2">
              <Star className="h-4 w-4" />
              Reviews
            </TabsTrigger>
            <TabsTrigger value="seat-room" className="flex items-center gap-2">
              <Settings className="h-4 w-4" />
              Seat/Room
            </TabsTrigger>
            <TabsTrigger value="pricing" className="flex items-center gap-2">
              <TrendingUp className="h-4 w-4" />
              Pricing
            </TabsTrigger>
            <TabsTrigger value="recommendations" className="flex items-center gap-2">
              <Sparkles className="h-4 w-4" />
              AI Picks
            </TabsTrigger>
          </TabsList>

          <TabsContent value="overview" className="space-y-6">
            <div className="grid grid-cols-1 md:grid-cols-3 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Calendar className="h-5 w-5" />
                    Total Bookings
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-3xl font-bold">{userBookings.length}</div>
                  <p className="text-sm text-gray-600">
                    {userBookings.filter(b => (b.status || 'confirmed') === 'confirmed').length} active
                  </p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <CreditCard className="h-5 w-5" />
                    Total Spent
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-3xl font-bold">
                    ${userBookings.reduce((sum, b) => sum + b.totalPrice, 0).toFixed(2)}
                  </div>
                  <p className="text-sm text-gray-600">This year</p>
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle className="flex items-center gap-2">
                    <Star className="h-5 w-5" />
                    Reviews Written
                  </CardTitle>
                </CardHeader>
                <CardContent>
                  <div className="text-3xl font-bold">0</div>
                  <p className="text-sm text-gray-600">Share your experiences</p>
                </CardContent>
              </Card>
            </div>

            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>Recent Bookings</CardTitle>
                </CardHeader>
                <CardContent>
                  {userBookings.slice(0, 3).map((booking) => (
                    <div key={booking.bookingId} className="flex items-center justify-between py-2 border-b last:border-b-0">
                      <div className="flex items-center gap-3">
                        {booking.type === 'flight' ? <Plane className="h-4 w-4" /> : <Hotel className="h-4 w-4" />}
                        <div>
                          <p className="font-medium">{booking.type} booking</p>
                          <p className="text-sm text-gray-600">{booking.travelDate ? new Date(booking.travelDate).toLocaleDateString() : 'N/A'}</p>
                        </div>
                      </div>
                      <span className={`px-2 py-1 rounded text-xs ${getStatusColor(booking.status || 'confirmed')}`}>
                        {booking.status || 'confirmed'}
                      </span>
                    </div>
                  ))}
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Quick Actions</CardTitle>
                </CardHeader>
                <CardContent className="space-y-2">
                  <Button 
                    className="w-full justify-start" 
                    variant="outline"
                    onClick={() => setActiveTab('flight-status')}
                  >
                    <Plane className="h-4 w-4 mr-2" />
                    Check Flight Status
                  </Button>
                  <Button 
                    className="w-full justify-start" 
                    variant="outline"
                    onClick={() => setActiveTab('reviews')}
                  >
                    <Star className="h-4 w-4 mr-2" />
                    Write a Review
                  </Button>
                  <Button 
                    className="w-full justify-start" 
                    variant="outline"
                    onClick={() => setActiveTab('pricing')}
                  >
                    <TrendingUp className="h-4 w-4 mr-2" />
                    Track Prices
                  </Button>
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          <TabsContent value="bookings" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Your Bookings</CardTitle>
              </CardHeader>
              <CardContent>
                {loading ? (
                  <div className="flex justify-center p-8">
                    <div className="animate-spin rounded-full h-8 w-8 border-b-2 border-blue-600"></div>
                  </div>
                ) : userBookings.length === 0 ? (
                  <div className="text-center py-8">
                    <Calendar className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                    <h3 className="text-lg font-medium text-gray-600 mb-2">No bookings yet</h3>
                    <p className="text-gray-500">Start planning your next trip!</p>
                  </div>
                ) : (
                  <div>
                    {userBookings.map((booking) => (
                      <BookingCard key={booking.bookingId} booking={booking} />
                    ))}
                  </div>
                )}
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="flight-status" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Flight Status Tracker</CardTitle>
              </CardHeader>
              <CardContent>
                <FlightStatusTracker
                  userFlights={userBookings
                    .filter(b => b.type === 'flight' && (b.status || 'confirmed') === 'confirmed' && b.itemId)
                    .map(b => b.itemId!)
                  }
                />
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="reviews" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Reviews & Ratings</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-6">
                  <p className="text-gray-600">Select a booking to write or view reviews:</p>
                  {userBookings
                    .filter(b => (b.status || 'confirmed') === 'confirmed')
                    .map((booking) => (
                      <div key={booking.bookingId} className="border rounded-lg p-4">
                        <div className="flex items-center justify-between mb-4">
                          <div className="flex items-center gap-2">
                            {booking.type === 'flight' ? <Plane className="h-5 w-5" /> : <Hotel className="h-5 w-5" />}
                            <span className="font-medium">{booking.type} - {booking.itemId}</span>
                          </div>
                          <Button size="sm">Write Review</Button>
                        </div>
                        <ReviewSystem
                          itemId={booking.itemId || booking.bookingId}
                          itemType={booking.type as 'flight' | 'hotel'}
                          currentUserId={currentUserId}
                        />
                      </div>
                    ))}
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="seat-room" className="space-y-6">
            <div className="grid grid-cols-1 lg:grid-cols-2 gap-6">
              <Card>
                <CardHeader>
                  <CardTitle>Flight Seat Selection</CardTitle>
                </CardHeader>
                <CardContent>
                  {userBookings.filter(b => b.type === 'flight' && (b.status || 'confirmed') === 'confirmed' && b.itemId).length > 0 ? (
                    <SeatMap
                      flightId={userBookings.find(b => b.type === 'flight' && (b.status || 'confirmed') === 'confirmed' && b.itemId)?.itemId || ''}
                      userId={currentUserId}
                    />
                  ) : (
                    <div className="text-center py-8">
                      <Plane className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                      <p className="text-gray-600">No confirmed flight bookings</p>
                    </div>
                  )}
                </CardContent>
              </Card>

              <Card>
                <CardHeader>
                  <CardTitle>Hotel Room Selection</CardTitle>
                </CardHeader>
                <CardContent>
                  {userBookings.filter(b => b.type === 'hotel' && (b.status || 'confirmed') === 'confirmed' && b.itemId).length > 0 ? (
                    <RoomSelector
                      hotelId={userBookings.find(b => b.type === 'hotel' && (b.status || 'confirmed') === 'confirmed' && b.itemId)?.itemId || ''}
                      userId={currentUserId}
                    />
                  ) : (
                    <div className="text-center py-8">
                      <Hotel className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                      <p className="text-gray-600">No confirmed hotel bookings</p>
                    </div>
                  )}
                </CardContent>
              </Card>
            </div>
          </TabsContent>

          <TabsContent value="pricing" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>Price Tracking</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-6">
                  <p className="text-gray-600">Track price changes for your bookings:</p>
                  {userBookings.filter(b => (b.status || 'confirmed') === 'confirmed' && b.itemId).length > 0 ? (
                    userBookings
                      .filter(b => (b.status || 'confirmed') === 'confirmed' && b.itemId)
                      .map((booking) => (
                        <div key={booking.bookingId} className="border rounded-lg p-4">
                          <div className="flex items-center gap-2 mb-4">
                            {booking.type === 'flight' ? <Plane className="h-5 w-5" /> : <Hotel className="h-5 w-5" />}
                            <span className="font-medium">{booking.type} - {booking.itemId}</span>
                          </div>
                          <PriceTracker
                            itemId={booking.itemId!}
                            itemType={booking.type as 'flight' | 'hotel'}
                            travelDate={booking.travelDate || new Date().toISOString()}
                            userId={currentUserId}
                          />
                        </div>
                      ))
                  ) : (
                    <div className="text-center py-8">
                      <TrendingUp className="h-12 w-12 text-gray-400 mx-auto mb-4" />
                      <h3 className="text-lg font-medium text-gray-600 mb-2">No bookings to track</h3>
                      <p className="text-gray-500">Make a booking to start tracking prices!</p>
                    </div>
                  )}
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="recommendations" className="space-y-6">
            <Card>
              <CardHeader>
                <CardTitle>AI Recommendations</CardTitle>
              </CardHeader>
              <CardContent>
                <AIRecommendations
                  userId={currentUserId}
                  onItemClick={(itemId, itemType) => {
                    console.log(`Clicked ${itemType}: ${itemId}`);
                    // Navigate to item details
                  }}
                />
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default Dashboard;