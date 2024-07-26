import React, { useEffect, useState } from "react";
import Navbar from "../../components/NavbarSuper";
import Sidebar from "../../components/SidebarUser";
import axios from "axios";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faUsers,
  faClipboardUser,
  faUser,
} from "@fortawesome/free-solid-svg-icons";
import Swal from "sweetalert2";
import { API_DUMMY } from "../../utils/api";
// import jwt from 'jsonwebtoken';

function DashboardSA() {
  const [currentDateTime, setCurrentDateTime] = useState(new Date());
  const [userData, setUserData] = useState([]);
  const [admin, setAdmin] = useState([]);
  const [absenData, setAbsenData] = useState([]);
  const [jabatanData, setJabatanData] = useState([]);
  const [lokasiData, setLokasiData] = useState([]);
  const [organisasiData, setOrganisasiData] = useState([]);
  const [username, setUsername] = useState("");
  const token = localStorage.getItem("token");
  const idSuperAdmin = localStorage.getItem("superadminId");
  const id = localStorage.getItem("superadminId");

  useEffect(() => {
    const interval = setInterval(() => {
      setCurrentDateTime(new Date());
    }, 1000); // Update every second

    return () => clearInterval(interval);
  }, []);

  const addLeadingZero = (num) => (num < 10 ? "0" + num : num);

  const day = currentDateTime.toLocaleDateString("id-ID", { weekday: "long" });
  const date = currentDateTime.toLocaleDateString("id-ID", {
    day: "numeric",
    month: "long",
    year: "numeric",
  });
  const time =
    addLeadingZero(currentDateTime.getHours()) +
    ":" +
    addLeadingZero(currentDateTime.getMinutes()) +
    ":" +
    addLeadingZero(currentDateTime.getSeconds());

  const fetchData = async (url, setter) => {
    const token = localStorage.getItem("token");
    try {
      const response = await axios.get(url, {
        headers: {
          Authorization: `Bearer ${token}`,
        },
      });
      setter(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  const getUser = () =>
    fetchData(`${API_DUMMY}/api/user/get-allUser`, setUserData);
  const getAbsensi = () =>
    fetchData(`${API_DUMMY}/api/absensi/getAll`, setAbsenData);
  const getJabatan = () =>
    fetchData(`${API_DUMMY}/api/jabatan/all`, setJabatanData);
  const getLokasi = () =>
    fetchData(`${API_DUMMY}/api/lokasi/getall`, setLokasiData);
  const getOrganisasi = () =>
    fetchData(
      `${API_DUMMY}/api/organisasi/superadmin/${id}`,
      setOrganisasiData
    );

  const getUsername = async () => {
    try {
      const response = await axios.get(
        `${API_DUMMY}/api/superadmin/getbyid/${id}`,
        {
          headers: {
            Authorization: `Bearer ${token}`,
          },
        }
      );
      setUsername(response.data.username);
    } catch (error) {
      console.error("Error fetching username:", error);
    }
  };

  const getAdmin = async () => {
    const token = localStorage.getItem("token");
    const idSuperAdmin = localStorage.getItem("superadminId");

    try {
      const response = await axios.get(
        `${API_DUMMY}/api/admin/get-all-by-super/${idSuperAdmin}`,
        {
          headers: {
            Authorization: `${token}`,
          },
        }
      );

      setAdmin(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  const getOrganisasiSA = async () => {
    const idSuperAdmin = localStorage.getItem("superadminId");
    const token = localStorage.getItem("token");

    try {
      const response = await axios.get(
        `${API_DUMMY}/api/organisasi/superadmin/${idSuperAdmin}`,
        {
          headers: {
            Authorization: `${token}`,
          },
        }
      );

      setOrganisasiData(response.data);
    } catch (error) {
      console.error("Error fetching data:", error);
    }
  };

  const formatDate = (tanggal) => {
    const date = new Date(tanggal);
    return date.toLocaleDateString("id-ID", {
      day: "numeric",
      month: "long",
      year: "numeric",
    });
  };

  useEffect(() => {
    getUser();
    getAbsensi();
    getUsername();
    getJabatan();
    getLokasi();
    getOrganisasi();
    getAdmin();
    getOrganisasiSA();
  }, []);

  useEffect(() => {
    if (localStorage.getItem("loginSuccess") === "true") {
      Swal.fire({
        icon: "success",
        title: "Berhasil masuk!",
      });
      localStorage.removeItem("loginSuccess");
    }
  }, []);
  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <Navbar />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className="content-page container p-8 ml-0 md:ml-64 mt-12">
          <div className="mt-5 w-full">
            <div className="p-4 text-center bg-indigo-300 border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
              <h2 className="text-2xl font-semibold mb-4">
                Selamat Datang di Absensi
                <span> @{username}</span>
              </h2>
              <a className="profile-menu-link">{day}, </a>
              <a className="profile-menu-link active">{date} - </a>
              <a className="profile-menu-link">{time}</a>
            </div>
          </div>

          <div className="grid grid-cols-1 gap-4 lg:grid-cols-3 lg:gap-8 mt-12">
            <div className="pl-2 h-32 bg-indigo-500 rounded-lg shadow-md md:w-auto">
              <div className="flex w-full h-full py-2 px-4 bg-gray-100 rounded-lg justify-between">
                <div className="my-auto">
                  <p className="font-bold">Organisasi</p>
                  <p className="text-lg">Jumlah Organisasi</p>
                  <p className="text-lg">{organisasiData.length}</p>
                </div>
                <div className="my-auto">
                  <FontAwesomeIcon icon={faUsers} size="2x" />
                </div>
              </div>
            </div>
            <div className="pl-2 h-32 bg-indigo-500 rounded-lg shadow-md md:w-auto">
              <div className="flex w-full h-full py-2 px-4 bg-gray-100 rounded-lg justify-between">
                <div className="my-auto">
                  <p className="font-bold">Admin</p>
                  <p className="text-lg">Jumlah Admin</p>
                  <p className="text-lg">{admin.length}</p>
                </div>
                <div className="my-auto">
                  <FontAwesomeIcon icon={faClipboardUser} size="2x" />
                </div>
              </div>
            </div>
            <div className="pl-2 h-32 bg-indigo-500 rounded-lg shadow-md md:w-auto">
              <div className="flex w-full h-full py-2 px-4 bg-gray-100 rounded-lg justify-between">
                <div className="my-auto">
                  <p className="font-bold">User</p>
                  <p className="text-lg">Jumlah User</p>
                  <p className="text-lg">{userData.length}</p>
                </div>
                <div className="my-auto">
                  <FontAwesomeIcon icon={faUser} size="2x" />
                </div>
              </div>
            </div>
          </div>

          <br />

          {/* Tabel Absensi */}
          <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
            <div className="flex justify-between">
              <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                Data Admin
              </h6>
            </div>
            <hr />

            {/* <!-- Tabel --> */}
            <div className="relative overflow-x-auto mt-5">
              <table
                id="dataKaryawan"
                className="w-full text-sm text-left text-gray-500 dark:text-gray-400"
              >
                {/* <!-- Tabel Head --> */}
                <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                  <tr>
                    <th scope="col" className="px-6 py-3">
                      No
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Email
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Username
                    </th>
                  </tr>
                </thead>
                {/* <!-- Tabel Body --> */}
                <tbody className="text-left">
                  {admin.map((admin, index) => (
                    <tr
                      className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
                      key={index}
                    >
                      <th
                        scope="row"
                        className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white"
                      >
                        {index + 1}
                      </th>
                      <td className="px-6 py-4">
                        <a
                          href="/cdn-cgi/l/email-protection"
                          className="__cf_email__"
                          data-cfemail="5a363b23363b1a3d373b333674393537"
                        >
                          {admin.email}
                        </a>
                      </td>
                      <td className="px-6 py-4 capitalize">{admin.username}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
          <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700 mt-10">
            <div className="flex justify-between">
              <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                Data Organisasi
              </h6>
            </div>
            <hr />

            {/* <!-- Tabel --> */}
            <div className="relative overflow-x-auto mt-5">
              <table
                id="dataKaryawan"
                className="w-full text-sm text-left text-gray-500 dark:text-gray-400"
              >
                {/* <!-- Tabel Head --> */}
                <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                  <tr>
                    <th scope="col" className="px-6 py-3">
                      No
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Admin
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Nama
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Alamat
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Telepon
                    </th>
                    <th scope="col" className="px-6 py-3">
                      Email
                    </th>
                  </tr>
                </thead>
                {/* <!-- Tabel Body --> */}
                <tbody className="text-left">
                  {organisasiData.map((admin, index) => (
                    <tr
                      className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
                      key={index}
                    >
                      <th
                        scope="row"
                        className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white"
                      >
                        {index + 1}
                      </th>
                      <td className="px-6 py-4 capitalize">
                        {admin.admin.username}
                      </td>
                      <td className="px-6 py-4 capitalize">
                        {admin.namaOrganisasi}
                      </td>
                      <td className="px-6 py-4 capitalize">{admin.alamat}</td>
                      <td className="px-6 py-4">{admin.nomerTelepon}</td>
                      <td className="px-6 py-4">{admin.emailOrganisasi}</td>
                    </tr>
                  ))}
                </tbody>
              </table>
            </div>
          </div>
          <br />
        </div>
      </div>
    </div>
  );
}

export default DashboardSA;
