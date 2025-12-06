import { useEffect, useState } from 'react';
import { Line } from 'react-chartjs-2';
import axios from 'axios';
import { Chart as ChartJS, CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend } from 'chart.js';

ChartJS.register(CategoryScale, LinearScale, PointElement, LineElement, Title, Tooltip, Legend);

export default function Dashboard() {
    const [stats, setStats] = useState({ totalRevenue: 0, orderCount: 0 });
    // Hardcoded login for demo
    const shop = "my-test-store.myshopify.com";

    useEffect(() => {
        // Pass Tenant ID in header
        axios.get('http://localhost:8080/api/insights', { headers: { 'X-Tenant-ID': shop } })
            .then(res => setStats(res.data));
    }, []);

    const chartData = {
        labels: ['Mon', 'Tue', 'Wed', 'Thu', 'Fri'],
        datasets: [{
            label: 'Sales (USD)',
            data: [120, 190, 30, 50, stats.totalRevenue], // Mock data + Real data mix
            borderColor: 'rgb(75, 192, 192)',
        }]
    };

    return (
        <div className="p-8 font-sans">
            <h1 className="text-3xl font-bold mb-6">Xeno Insights: {shop}</h1>
            <div className="grid grid-cols-2 gap-4 mb-8">
                <div className="p-6 bg-blue-100 rounded-lg shadow">
                    <h3>Total Revenue</h3>
                    <p className="text-2xl font-bold">${stats.totalRevenue}</p>
                </div>
                <div className="p-6 bg-green-100 rounded-lg shadow">
                    <h3>Total Orders</h3>
                    <p className="text-2xl font-bold">{stats.orderCount}</p>
                </div>
            </div>
            <div className="h-64 bg-white p-4 rounded shadow">
                <Line data={chartData} options={{ maintainAspectRatio: false }} />
            </div>
        </div>
    );
}