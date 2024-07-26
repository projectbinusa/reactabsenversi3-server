import React, { useEffect, useState } from "react";
import NavbarSuper from "../../../components/NavbarSuper";
import Sidebar from "../../../components/SidebarUser";
import { FontAwesomeIcon } from "@fortawesome/react-fontawesome";
import {
  faInfo,
  faPenToSquare,
  faPlus,
  faTrash,
} from "@fortawesome/free-solid-svg-icons";
import axios from "axios";
import Swal from "sweetalert2";

import { API_DUMMY } from "../../../utils/api";

import { Pagination } from "flowbite-react";


function LokasiSA() {
  const [lokasiList, setLokasiList] = useState([]);
  const [jumlahKaryawan, setJumlahKaryawan] = useState({});
  const [searchTerm, setSearchTerm] = useState("");
  const [limit, setLimit] = useState(5);
  const [currentPage, setCurrentPage] = useState(1);
  const [totalPages, setTotalPages] = useState(1);

  const idSuperAdmin = localStorage.getItem("superadminId");

  const getLokasiBySuperAdmin = async () => {
    try {
      const lok = await axios.get(
        `${API_DUMMY}/api/lokasi/superadmin/${idSuperAdmin}`
      );
      setLokasiList(lok.data);

      // Mengambil jumlah karyawan untuk setiap lokasi
      const jumlahKaryawanData = {};
      for (const lokasi of lok.data) {
        const kar = await axios.get(
          `${API_DUMMY}/api/user/${lokasi.admin.id}/users`
        );
        jumlahKaryawanData[lokasi.admin.id] = kar.data.length;
      }
      setJumlahKaryawan(jumlahKaryawanData);
    } catch (error) {
      console.log(error);
    }
  };

  useEffect(() => {
    getLokasiBySuperAdmin();
  }, []);

  const deleteLokasi = async (idLokasi) => {
    try {
      const result = await Swal.fire({
        title: "Anda yakin?",
        text: "Anda tidak dapat mengembalikan data yang telah dihapus!",
        icon: "warning",
        showCancelButton: true,
        confirmButtonColor: "#3085d6",
        cancelButtonColor: "#d33",
        confirmButtonText: "Ya, hapus!",
        cancelButtonText: "Batal",
      });

      if (result.isConfirmed) {
        await axios.delete(
          `${API_DUMMY}/api/lokasi/delete/${idLokasi}`
        );
        Swal.fire("Dihapus!", "Data lokasi telah dihapus.", "success");
        window.location.reload();
      }
    } catch (error) {
      console.log(error);
      Swal.fire("Gagal!", "Gagal menghapus data.", "error");
    }
  };

  useEffect(() => {
    const filteredData = lokasiList.filter(
      (lokasi) =>
        lokasi.namaLokasi?.toLowerCase().includes(searchTerm.toLowerCase()) ||
        lokasi.alamat?.toLowerCase().includes(searchTerm.toLowerCase())
    );
    setTotalPages(Math.ceil(filteredData.length / limit));
  }, [searchTerm, limit, lokasiList]);

  const handleSearch = (event) => {
    setSearchTerm(event.target.value);
  };

  const handleLimitChange = (event) => {
    setLimit(parseInt(event.target.value));
    setCurrentPage(1); // Reset to the first page when limit changes
  };

  function onPageChange(page) {
    setCurrentPage(page);
  }

  const filteredLokasi = lokasiList.filter(
    (lokasi) =>
      lokasi.namaLokasi?.toLowerCase().includes(searchTerm.toLowerCase()) ||
      lokasi.alamat?.toLowerCase().includes(searchTerm.toLowerCase())
  );

  const paginatedLokasi = filteredLokasi.slice(
    (currentPage - 1) * limit,
    currentPage * limit
  );

  return (
    <div className="flex flex-col h-screen">
      <div className="sticky top-0 z-50">
        <NavbarSuper />
      </div>
      <div className="flex h-full">
        <div className="fixed">
          <Sidebar />
        </div>
        <div className="sm:ml-64 content-page container p-8 ml-0 md:ml-64 mt-12">
          <div className="p-5 mt-10">
            <div className="w-full p-4 text-center bg-white border border-gray-200 rounded-lg shadow sm:p-8 dark:bg-gray-800 dark:border-gray-700">
              <div className="flex justify-between">
                <h6 className="mb-2 text-xl font-bold text-gray-900 dark:text-white">
                  Data Lokasi
                </h6>
                <div className="flex items-center gap-2 mt-2">
                  <div className="relative w-64">
                    <input
                      type="search"
                      id="search-dropdown"
                      value={searchTerm}
                      onChange={handleSearch}
                      className="block p-2.5 w-full z-20 text-sm rounded-l-md text-gray-900 bg-gray-50 border-gray-300 focus:ring-blue-500 focus:border-blue-500 dark:bg-gray-700 dark:border-gray-600 dark:placeholder-gray-400 dark:text-white dark:focus:border-blue-500"
                      placeholder="Search name..."
                      required
                    />
                  </div>
                  <select
                    value={limit}
                    onChange={handleLimitChange}
                    className="flex-shrink-0 z-10 inline-flex rounded-r-md items-center py-2.5 px-4 text-sm font-medium text-gray-900 bg-gray-100 border border-gray-300 hover:bg-gray-200 focus:ring-4 focus:outline-none focus:ring-gray-100 dark:bg-gray-700 dark:hover:bg-gray-600 dark:focus:ring-gray-700 dark:text-white dark:border-gray-600"
                  >
                    <option value="5">05</option>
                    <option value="10">10</option>
                    <option value="20">20</option>
                    <option value="50">50</option>
                  </select>
                  <a
                    type="button"
                    href="/superadmin/addLokasi"
                    className="text-white bg-indigo-500 focus:ring-4 focus:ring-indigo-300 font-medium rounded-lg text-sm px-5 py-2.5 mr-2 mb-2 dark:bg-indigo-600 dark:hover:bg-indigo-700 focus:outline-none dark:focus:ring-indigo-800 mt-2"
                  >
                    <FontAwesomeIcon icon={faPlus} size="lg" />
                  </a>
                </div>
              </div>
              <hr />

              <div className="relative overflow-x-auto mt-5">
                <table
                  id="dataJabatan"
                  className="w-full text-sm text-left text-gray-500 dark:text-gray-400"
                >
                  <thead className="text-xs text-gray-700 uppercase bg-gray-50 dark:bg-gray-700 dark:text-gray-400">
                    <tr>
                      <th scope="col" className="px-6 py-3">
                        No
                      </th>
                      <th scope="col" className="px-6 py-3">
                        Nama Lokasi
                      </th>
                      <th scope="col" className="px-6 py-3">
                        Alamat
                      </th>
                      <th scope="col" className="px-6 py-3">
                        Jumlah Karyawan
                      </th>
                      <th scope="col" className="px-6 py-3">
                        Organisasi
                      </th>
                      <th scope="col" className="px-6 py-3 text-center">
                        Aksi
                      </th>
                    </tr>
                  </thead>
                  <tbody className="text-left">
                    {paginatedLokasi.map((lokasi, index) => (
                      <tr
                        key={lokasi.idLokasi}
                        className="bg-white border-b dark:bg-gray-800 dark:border-gray-700 hover:bg-gray-50 dark:hover:bg-gray-600"
                      >
                        <th
                          scope="row"
                          className="px-6 py-4 font-medium text-gray-900 whitespace-nowrap dark:text-white"
                        >
                          {(currentPage - 1) * limit + index + 1}
                        </th>
                        <td className="px-6 py-4">{lokasi.namaLokasi}</td>
                        <td className="px-6 py-4">{lokasi.alamat}</td>
                        <td className="px-6 py-4">
                          {jumlahKaryawan[lokasi.admin.id]}
                        </td>
                        <td className="px-6 py-4">
                          {lokasi.organisasi.namaOrganisasi}
                        </td>
                        <td className="py-3">
                          <div className="flex items-center -space-x-4 ml-12">
                            <a
                              href={`/superadmin/detailLokasi/${lokasi.idLokasi}`}
                            >
                              <button className="z-20 block rounded-full border-2 border-white bg-blue-100 p-4 text-blue-700 active:bg-blue-50">
                                <span className="relative inline-block">
                                  <FontAwesomeIcon
                                    icon={faInfo}
                                    className="h-4 w-4"
                                  />
                                </span>
                              </button>
                            </a>
                            <a
                              href={`/superadmin/editLokasi/${lokasi.idLokasi}`}
                            >
                              <button className="z-30 block rounded-full border-2 border-white bg-yellow-100 p-4 text-yellow-700 active:bg-red-50">
                                <span className="relative inline-block">
                                  <FontAwesomeIcon
                                    icon={faPenToSquare}
                                    className="h-4 w-4"
                                  />
                                </span>
                              </button>
                            </a>
                            <button
                              onClick={() => deleteLokasi(lokasi.idLokasi)}
                              className="z-30 block rounded-full border-2 border-white bg-red-100 p-4 text-red-700 active:bg-red-50"
                            >
                              <span className="relative inline-block">
                                <FontAwesomeIcon
                                  icon={faTrash}
                                  className="h-4 w-4"
                                />
                              </span>
                            </button>
                          </div>
                        </td>
                      </tr>
                    ))}
                  </tbody>
                </table>
              </div>
              <Pagination
                className="mt-5"
                layout="table"
                currentPage={currentPage}
                totalPages={totalPages}
                onPageChange={onPageChange}
                showIcons
              />
            </div>
          </div>
        </div>
      </div>
    </div>
  );
}

export default LokasiSA;
