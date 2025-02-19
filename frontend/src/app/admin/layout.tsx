import type { Metadata } from 'next'
import client from '@/lib/backend/client'
import { cookies } from 'next/headers'
import ClientLayout from './ClientLayout'
import '../globals.css'

export const metadata: Metadata = {
  title: 'Admin - Grids & Circles',
}

export default async function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  const response = await client.GET('/members/username', {
    headers: {
      cookie: (await cookies()).toString(),
    },
  })

  const userData = response.data
    ? response.data
    : {
        success: true,
        message: '',
      }

  return <ClientLayout userData={userData}>{children}</ClientLayout>
}
