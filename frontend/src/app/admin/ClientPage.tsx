'use client'

import client from '@/lib/backend/client'
import { useRouter } from 'next/navigation'
import { useEffect } from 'react'
import { useSearchParams } from 'next/navigation'

export default function ClientPage() {
  const searchParams = useSearchParams()

  useEffect(() => {
    const message = searchParams.get('message')
    if (message) {
      alert(message)

      window.history.replaceState({}, '', '/admin')
    }
  }, [searchParams])

  const router = useRouter()

  const handleSubmit = async (e: React.FormEvent<HTMLFormElement>) => {
    e.preventDefault()

    const form = e.target as HTMLFormElement
    let url

    if (form.username.value.length === 0) {
      alert('아이디를 입력해주세요.')

      form.username.focus()

      return
    }
    if (form.password.value.length === 0) {
      alert('비밀번호를 입력해주세요.')

      form.password.focus()

      return
    }

    const response = await client.POST('/members/login', {
      body: {
        username: form.username.value,
        password: form.password.value,
      },
    })
    if (response.error) {
      alert(response.error.message)

      return
    }
    alert(response.data.message)

    window.location.replace('/admin/products')
  }

  return (
    <>
      <form onSubmit={handleSubmit}>
        <div className="max-w-[500px] w-full absolute top-1/2 left-1/2 p-5 -translate-x-1/2 -translate-y-1/2">
          <h2 className="text-5xl font-extrabold mb-10 text-center">
            ADMIN LOGIN
          </h2>
          <div className="bg-white p-10 rounded-[8px] shadow-[0_0_10px_0_rgba(0,0,0,0.1)]">
            <div>
              <input
                type="text"
                name="username"
                id="username"
                className="p-2 h-[50px] w-full border-b border-[#ddd]"
                placeholder="아이디"
              />
            </div>
            <div className="mt-3">
              <input
                type="password"
                name="password"
                id="password"
                className="p-2 h-[50px] w-full border-b border-[#ddd]"
                placeholder="비밀번호"
              />
            </div>

            <div className="mt-10 text-center">
              <input
                type="submit"
                value="로그인"
                className="w-full h-[50px] bg-[#59473F] text-white rounded-[8px] h-[40px]"
              />
            </div>
          </div>
        </div>
      </form>
    </>
  )
}
