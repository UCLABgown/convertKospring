import client from '@/lib/backend/client'
import ClientPage from './ClientPage'
import { cookies } from 'next/headers'
import { redirect } from 'next/navigation'

export default async function Page() {
  const meResponse = await client.GET('/members/username', {
    headers: {
      cookie: await cookies().toString(),
    },
  })

  // 로그인되지 않은 경우 (error 객체가 있는 경우)
  if (meResponse.error) {
    return <ClientPage />
  }

  // 로그인된 경우
  const isAdmin = meResponse.data.message === 'admin'

  if (isAdmin) {
    // 관리자인 경우
    redirect('/admin/products')
  }

  return (
    <>
      <ClientPage />
    </>
  )
}
