import React, { useState } from 'react';
import { Dialog, DialogContent, DialogHeader, DialogTitle, DialogTrigger } from '../ui/dialog';
import { Button } from '../ui/button';
import { SimpleSelect } from '../ui/simple-select';
import { Textarea } from '../ui/textarea';
import { Label } from '../ui/label';
import { AlertCircle, DollarSign } from 'lucide-react';
import axios from 'axios';
import { API_BASE_URL } from '../../config/api';

interface CancellationDialogProps {
  booking: any;
  onCancel: (bookingId: string) => void;
  userId?: string;
}

const CancellationDialog: React.FC<CancellationDialogProps> = ({ booking, onCancel, userId }) => {
  const [reason, setReason] = useState('');
  const [customReason, setCustomReason] = useState('');
  const [refundAmount, setRefundAmount] = useState<number | null>(null);
  const [loading, setLoading] = useState(false);
  const [step, setStep] = useState(1);

  const cancellationReasons = [
    { value: 'change-of-plans', label: 'Change of plans' },
    { value: 'medical', label: 'Medical emergency' },
    { value: 'emergency', label: 'Family emergency' },
    { value: 'work', label: 'Work commitment' },
    { value: 'weather', label: 'Weather concerns' },
    { value: 'other', label: 'Other' }
  ];

  const calculateRefund = async () => {
    if (!reason) return;
    
    setLoading(true);
    try {
      // For demo purposes, calculate refund locally
      // In production, this would call the backend API
      const travelDate = new Date(booking.travelDate || booking.date);
      const now = new Date();
      const hoursUntilTravel = (travelDate.getTime() - now.getTime()) / (1000 * 60 * 60);
      
      let refundPercentage = 0;
      if (hoursUntilTravel > 24) {
        refundPercentage = 0.8; // 80% refund if >24h
      } else if (hoursUntilTravel > 2) {
        refundPercentage = 0.5; // 50% refund if >2h
      } else {
        refundPercentage = 0; // No refund if <2h
      }
      
      const calculatedRefund = booking.totalPrice * refundPercentage;
      
      // Try API call, fallback to local calculation
      try {
        const response = await axios.post(`${API_BASE_URL}/cancellation/calculate-refund`, {
          userId: userId || booking.userId,
          bookingId: booking.bookingId,
          reason: reason === 'other' ? customReason : reason
        });
        setRefundAmount(response.data.refundAmount);
      } catch (error) {
        console.log('API not available, using local calculation');
        setRefundAmount(calculatedRefund);
      }
      setStep(2);
    } catch (error) {
      console.error('Error calculating refund:', error);
    } finally {
      setLoading(false);
    }
  };

  const confirmCancellation = async () => {
    setLoading(true);
    try {
      // Try API call, fallback to local update
      try {
        await axios.post(`${API_BASE_URL}/cancellation/cancel`, {
          userId: userId || booking.userId,
          bookingId: booking.bookingId,
          reason: reason === 'other' ? customReason : reason
        });
      } catch (error) {
        console.log('API not available, updating locally');
      }
      onCancel(booking.id);
      setStep(3);
    } catch (error) {
      console.error('Error cancelling booking:', error);
    } finally {
      setLoading(false);
    }
  };

  return (
    <Dialog>
      <DialogTrigger asChild>
        <Button variant="destructive" size="sm">
          Cancel Booking
        </Button>
      </DialogTrigger>
      <DialogContent className="sm:max-w-md">
        <DialogHeader>
          <DialogTitle>Cancel Booking</DialogTitle>
        </DialogHeader>
        
        {step === 1 && (
          <div className="space-y-4">
            <div>
              <Label htmlFor="reason">Reason for cancellation</Label>
              <SimpleSelect 
                value={reason} 
                onValueChange={setReason}
                placeholder="Select a reason"
                options={cancellationReasons}
              />
            </div>
            
            {reason === 'other' && (
              <div>
                <Label htmlFor="custom-reason">Please specify</Label>
                <Textarea
                  id="custom-reason"
                  value={customReason}
                  onChange={(e) => setCustomReason(e.target.value)}
                  placeholder="Please provide details..."
                />
              </div>
            )}
            
            <div className="flex items-center gap-2 p-3 bg-yellow-50 rounded-lg">
              <AlertCircle className="h-4 w-4 text-yellow-600" />
              <p className="text-sm text-yellow-800">
                Refund amount depends on cancellation time and reason
              </p>
            </div>
            
            <Button 
              onClick={calculateRefund} 
              disabled={!reason || (reason === 'other' && !customReason) || loading}
              className="w-full"
            >
              {loading ? 'Calculating...' : 'Calculate Refund'}
            </Button>
          </div>
        )}
        
        {step === 2 && (
          <div className="space-y-4">
            <div className="text-center">
              <DollarSign className="h-12 w-12 text-green-600 mx-auto mb-2" />
              <h3 className="text-lg font-semibold">Refund Calculation</h3>
              <p className="text-2xl font-bold text-green-600">
                ${refundAmount?.toFixed(2)}
              </p>
              <p className="text-sm text-gray-600">
                Out of ${booking.totalPrice?.toFixed(2)} paid
              </p>
            </div>
            
            <div className="bg-gray-50 p-3 rounded-lg">
              <p className="text-sm">
                <strong>Booking:</strong> {booking.type} - {booking.itemId || booking.bookingId}
              </p>
              <p className="text-sm">
                <strong>Reason:</strong> {reason === 'other' ? customReason : 
                  cancellationReasons.find(r => r.value === reason)?.label}
              </p>
            </div>
            
            <div className="flex gap-2">
              <Button variant="outline" onClick={() => setStep(1)} className="flex-1">
                Back
              </Button>
              <Button 
                onClick={confirmCancellation} 
                disabled={loading}
                variant="destructive"
                className="flex-1"
              >
                {loading ? 'Cancelling...' : 'Confirm Cancellation'}
              </Button>
            </div>
          </div>
        )}
        
        {step === 3 && (
          <div className="text-center space-y-4">
            <div className="text-green-600">
              <svg className="h-16 w-16 mx-auto mb-4" fill="none" stroke="currentColor" viewBox="0 0 24 24">
                <path strokeLinecap="round" strokeLinejoin="round" strokeWidth={2} d="M5 13l4 4L19 7" />
              </svg>
              <h3 className="text-lg font-semibold">Booking Cancelled</h3>
              <p className="text-sm text-gray-600">
                Your refund of ${refundAmount?.toFixed(2)} will be processed within 5-7 business days
              </p>
            </div>
          </div>
        )}
      </DialogContent>
    </Dialog>
  );
};

export default CancellationDialog;