import React from 'react';
import { Tabs, TabsContent, TabsList, TabsTrigger } from '../components/ui/tabs';
import CancellationDialog from '../components/CancellationRefund/CancellationDialog';
import ReviewSystem from '../components/Reviews/ReviewSystem';
import FlightStatusTracker from '../components/FlightStatus/FlightStatusTracker';
import SeatMap from '../components/SeatSelection/SeatMap';
import RoomSelector from '../components/RoomSelection/RoomSelector';
import PriceTracker from '../components/Pricing/PriceTracker';
import AIRecommendations from '../components/Recommendations/AIRecommendations';
import { Card, CardContent, CardHeader, CardTitle } from '../components/ui/card';

const TestComponents: React.FC = () => {
  const mockBooking = {
    bookingId: 'booking123',
    type: 'flight',
    itemId: 'flight_ai101',
    date: '2025-01-01T10:00:00',
    travelDate: '2025-02-15T10:00:00',
    quantity: 2,
    originalPrice: 500,
    totalPrice: 450,
    status: 'confirmed'
  };

  return (
    <div className="min-h-screen bg-gray-50 p-8">
      <div className="container mx-auto">
        <h1 className="text-3xl font-bold mb-8">Component Testing Page</h1>
        
        <Tabs defaultValue="cancellation" className="space-y-6">
          <TabsList className="grid w-full grid-cols-7">
            <TabsTrigger value="cancellation">Cancellation</TabsTrigger>
            <TabsTrigger value="reviews">Reviews</TabsTrigger>
            <TabsTrigger value="flight-status">Flight Status</TabsTrigger>
            <TabsTrigger value="seat-map">Seat Map</TabsTrigger>
            <TabsTrigger value="room-selector">Room Selector</TabsTrigger>
            <TabsTrigger value="pricing">Pricing</TabsTrigger>
            <TabsTrigger value="ai-recommendations">AI Recommendations</TabsTrigger>
          </TabsList>

          <TabsContent value="cancellation">
            <Card>
              <CardHeader>
                <CardTitle>Cancellation & Refund System</CardTitle>
              </CardHeader>
              <CardContent>
                <div className="space-y-4">
                  <div className="p-4 border rounded-lg">
                    <h3 className="font-semibold mb-2">Sample Booking</h3>
                    <p>Flight AI101 - 2 passengers</p>
                    <p>Travel Date: Feb 15, 2025</p>
                    <p>Price: $450</p>
                    <div className="mt-4">
                      <CancellationDialog 
                        booking={mockBooking} 
                        onCancel={(id) => console.log('Cancelled:', id)} 
                      />
                    </div>
                  </div>
                </div>
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="reviews">
            <Card>
              <CardHeader>
                <CardTitle>Review & Rating System</CardTitle>
              </CardHeader>
              <CardContent>
                <ReviewSystem 
                  itemId="flight_ai101"
                  itemType="flight"
                  currentUserId="user123"
                />
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="flight-status">
            <Card>
              <CardHeader>
                <CardTitle>Live Flight Status</CardTitle>
              </CardHeader>
              <CardContent>
                <FlightStatusTracker userFlights={['flight_ai101', 'flight_6e202']} />
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="seat-map">
            <Card>
              <CardHeader>
                <CardTitle>Seat Selection</CardTitle>
              </CardHeader>
              <CardContent>
                <SeatMap 
                  flightId="flight_ai101"
                  userId="user123"
                  onSeatSelect={(seats, price) => console.log('Selected seats:', seats, 'Price:', price)}
                />
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="room-selector">
            <Card>
              <CardHeader>
                <CardTitle>Room Selection</CardTitle>
              </CardHeader>
              <CardContent>
                <RoomSelector 
                  hotelId="hotel_taj_mumbai"
                  userId="user123"
                  onRoomSelect={(room, type, price) => console.log('Selected room:', room, type, price)}
                />
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="pricing">
            <Card>
              <CardHeader>
                <CardTitle>Dynamic Pricing</CardTitle>
              </CardHeader>
              <CardContent>
                <PriceTracker 
                  itemId="flight_ai101"
                  itemType="flight"
                  travelDate="2025-02-15T10:00:00"
                  userId="user123"
                />
              </CardContent>
            </Card>
          </TabsContent>

          <TabsContent value="ai-recommendations">
            <Card>
              <CardHeader>
                <CardTitle>AI Recommendations</CardTitle>
              </CardHeader>
              <CardContent>
                <AIRecommendations 
                  userId="user123"
                  onItemClick={(itemId, itemType) => console.log('Clicked:', itemType, itemId)}
                />
              </CardContent>
            </Card>
          </TabsContent>
        </Tabs>
      </div>
    </div>
  );
};

export default TestComponents;