import type { Metadata } from 'next'
import { Noto_Sans_KR } from 'next/font/google'
import './globals.css'

const notoSansKr = Noto_Sans_KR({
  weight: ['100', '400', '600', '700', '900'],
  subsets: ['latin'],
})

export const metadata: Metadata = {
  title: 'Grids & Circles',
}

export default function RootLayout({
  children,
}: Readonly<{
  children: React.ReactNode
}>) {
  return (
    <html lang="ko">
      <body
        className={`${notoSansKr.className} antialiased flex flex-col min-h-[100dvh] bg-[#f5f5f5]`}
      >
        {children}

        {/*  Footer  */}
        <footer className="p-8 mt-20 text-center border-t border-[#ccc] text-sm text-white bg-[#59473F]">
          ⓒ 2025 NBE3-4-1-Team16 ALL RIGHTS RESERVED.
        </footer>
      </body>
    </html>
  )
}
