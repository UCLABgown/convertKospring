'use client'

import { components } from '@/lib/backend/apiV1/schema'
import client from '@/lib/backend/client'
import Link from 'next/link'
import { usePathname } from 'next/navigation'

export default function ClientLayout({
  children,
  userData,
}: Readonly<{
  children: React.ReactNode
  userData: components['schemas']['ApiResponseString']
}>) {
  const pathname = usePathname()
  const isLogin = userData.message?.length !== 0
  const showLogout = isLogin && pathname !== '/admin'

  const logout = async () => {
    const response = await client.DELETE('/members/logout')

    if (response.error) {
      alert(response.error.message)

      return
    }

    window.location.replace('/main')
  }

  return (
    <>
      <header className="border-[#ccc] border-b p-5 flex bg-white">
        <h1 className="text-4xl font-extrabold align-middle">
          <a href="/main">Grids & Circles</a>
          <a href="/admin/products">
            <span className="text-sm text-gray-400 font-normal"> ADMIN</span>
          </a>
        </h1>
        <nav className="ml-auto flex gap-5 items-center">
          <Link
            href="/admin/order/list"
            className="align-middle block text-lg text-gray-800 font-semibold"
          >
            주문 확인
          </Link>
          {showLogout && (
            <button
              onClick={logout}
              className="align-middle block text-lg text-gray-800 font-semibold"
            >
              로그아웃
            </button>
          )}
        </nav>
      </header>
      <main className="flex-grow p-5 bg-[#f5f5f5] relative">
        <div className="max-w-[1800px] mx-auto">{children}</div>
      </main>
    </>
  )
}
