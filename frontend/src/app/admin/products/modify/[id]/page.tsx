import React, { useEffect, useState } from 'react'
import client from '@/lib/backend/client'
import ClientPage from './ClientPage'
import { cookies } from 'next/headers'
import axios from 'axios'
export default async function Page({ params }: { params: { id: number } }) {
  const { id } = await params
  const responseCategories = await client.GET('/products/categories')

  const responseBodyCategory = responseCategories.data!!

  const response = await client.GET('/products/{id}', {
    params: {
      path: {
        id: Number(id),
      },
    },
    headers: {
      cookie: (await cookies()).toString(),
    },
  })
  if (response.error) {
    return <>{response.error.message}</>
  }
  const responseBody = response.data!!
  return (
    <>
      <ClientPage
        responseBody={responseBody}
        id={id.toString()}
        responseBodyCategory={responseBodyCategory}
      />
    </>
  )
}
